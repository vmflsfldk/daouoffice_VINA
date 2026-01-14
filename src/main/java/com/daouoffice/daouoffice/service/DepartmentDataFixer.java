package com.daouoffice.daouoffice.service;

import com.daouoffice.daouoffice.entity.Department;
import com.daouoffice.daouoffice.repository.DepartmentRepository;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@ConditionalOnProperty(value = "department.data-fixer.enabled", havingValue = "true")
public class DepartmentDataFixer {
    private static final Logger log = LoggerFactory.getLogger(DepartmentDataFixer.class);

    private static final String GLOBAL_DIVISION_NAME = "글로벌사업부";
    private static final String BUSINESS_STRATEGY_OFFICE = "사업전략실";
    private static final String IPJT_TEAM = "I PJT팀";

    private final DepartmentRepository deptRepo;
    private final boolean writeEnabled;

    public DepartmentDataFixer(DepartmentRepository deptRepo,
                               @Value("${department.data-fixer.write-enabled:false}") boolean writeEnabled) {
        this.deptRepo = deptRepo;
        this.writeEnabled = writeEnabled;
    }

    @PostConstruct
    @Transactional
    public void normalizeGlobalDivisionParent() {
        try {
            Optional<Department> globalOpt = findDepartmentByName(GLOBAL_DIVISION_NAME);
            if (globalOpt.isEmpty()) {
                log.info("'{}' not found; no parent fix needed.", GLOBAL_DIVISION_NAME);
                return;
            }

            Department globalDivision = globalOpt.get();
            Optional<Department> strategyOpt = findDepartmentByName(BUSINESS_STRATEGY_OFFICE);
            Optional<Department> ipjtOpt = findDepartmentByName(IPJT_TEAM);

            boolean pointingToIpjt = ipjtOpt.map(Department::getId)
                    .map(id -> Objects.equals(globalDivision.getParentId(), id))
                    .orElse(false);

            if (strategyOpt.isEmpty()) {
                if (pointingToIpjt) {
                    if (!writeEnabled) {
                        log.warn("Write access disabled; unable to detach '{}' from '{}' in go_departments.",
                                GLOBAL_DIVISION_NAME, IPJT_TEAM);
                        return;
                    }
                    detachFromIpjt(globalDivision, ipjtOpt);
                } else {
                    log.warn("Cannot fix '{}' because '{}' is missing in go_departments.",
                            GLOBAL_DIVISION_NAME, BUSINESS_STRATEGY_OFFICE);
                }
                return;
            }

            Department correctParent = strategyOpt.get();

            String expectedPath = buildChildPath(correctParent, globalDivision);
            String normalizedCurrentPath = stripTrailingSlash(globalDivision.getPath());
            boolean pathMismatch = normalizedCurrentPath == null || !normalizedCurrentPath.equals(expectedPath);
            boolean parentMismatch = !Objects.equals(globalDivision.getParentId(), correctParent.getId());

            if (pointingToIpjt || parentMismatch || pathMismatch) {
                if (!writeEnabled) {
                    log.warn("Write access disabled; skipping update for '{}' in go_departments.", GLOBAL_DIVISION_NAME);
                    return;
                }
                Long previousParent = globalDivision.getParentId();
                String previousPath = globalDivision.getPath();

                globalDivision.setParentId(correctParent.getId());
                globalDivision.setPath(expectedPath);
                deptRepo.save(globalDivision);

                log.info(
                        "Updated '{}' (id={}) parent from {} to {} and path from '{}' to '{}' to align with '{}' (id={}).",
                        GLOBAL_DIVISION_NAME, globalDivision.getId(), previousParent, correctParent.getId(), previousPath,
                        expectedPath, BUSINESS_STRATEGY_OFFICE, correctParent.getId()
                );
            }
        } catch (Exception ex) {
            log.error("Failed to normalize '{}' parent relationship; skipping data fix.", GLOBAL_DIVISION_NAME, ex);
        }
    }

    private void detachFromIpjt(Department globalDivision, Optional<Department> ipjtOpt) {
        Long previousParent = globalDivision.getParentId();
        String previousPath = globalDivision.getPath();

        globalDivision.setParentId(null);
        globalDivision.setPath(String.valueOf(globalDivision.getId()));
        deptRepo.save(globalDivision);

        log.info(
                "Detached '{}' (id={}) from '{}' (id={}) and reset path from '{}' to '{}' while '{}' is missing.",
                GLOBAL_DIVISION_NAME, globalDivision.getId(), IPJT_TEAM, ipjtOpt.map(Department::getId).orElse(null),
                previousPath, globalDivision.getPath(), BUSINESS_STRATEGY_OFFICE
        );
    }

    private Optional<Department> findDepartmentByName(String name) {
        List<Department> departments = deptRepo.findAllByNameOrderByIdAsc(name);
        if (departments.isEmpty()) {
            return Optional.empty();
        }

        Department chosen = departments.get(0);
        if (departments.size() > 1) {
            log.warn("Found {} records for '{}' in go_departments; using id={} (lowest id).",
                    departments.size(), name, chosen.getId());
        }

        return Optional.of(chosen);
    }

    private String buildChildPath(Department parent, Department child) {
        String base = stripTrailingSlash(parent.getPath());
        if (base == null || base.isBlank()) {
            return String.valueOf(child.getId());
        }
        return base + "/" + child.getId();
    }

    private String stripTrailingSlash(String path) {
        if (path == null) {
            return null;
        }
        int length = path.length();
        int end = length;
        while (end > 0 && path.charAt(end - 1) == '/') {
            end--;
        }
        return path.substring(0, end);
    }
}

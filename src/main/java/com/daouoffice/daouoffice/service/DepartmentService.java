package com.daouoffice.daouoffice.service;

import com.daouoffice.daouoffice.dto.DeptNodeDto;
import com.daouoffice.daouoffice.entity.Department;
import com.daouoffice.daouoffice.repository.DeptMemberRepository;
import com.daouoffice.daouoffice.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);
    private static final String GLOBAL_DIVISION_NAME = "글로벌사업부";
    private static final String IPJT_TEAM_NAME = "I PJT팀";

    private final DepartmentRepository deptRepo;
    private final DeptMemberRepository memberRepo;

    public DepartmentService(DepartmentRepository deptRepo,
                             DeptMemberRepository memberRepo) {
        this.deptRepo = deptRepo;
        this.memberRepo = memberRepo;
    }

    public List<DeptNodeDto> buildTreeWithTotal() {
        List<Department> depts = findDepartmentsExcludingMisplacedGlobalDivision();
        Map<Long, Department> deptById = depts.stream()
                .collect(Collectors.toMap(Department::getId, d -> d));
        Comparator<DeptNodeDto> nodeComparator = Comparator
                .comparing((DeptNodeDto dto) -> {
                    Department dept = deptById.get(dto.getId());
                    return dept != null ? dept.getPath() : null;
                }, Comparator.nullsLast(String::compareTo))
                .thenComparing(dto -> {
                    Department dept = deptById.get(dto.getId());
                    return dept != null ? dept.getSortOrder() : null;
                }, Comparator.nullsLast(Integer::compareTo));
        Map<Long, Long> directCount = memberRepo.findDirectCounts()
                .stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> ((Number) arr[1]).longValue()
                ));

        Map<Long, DeptNodeDto> nodeMap = new HashMap<>();
        Map<Long, List<DeptNodeDto>> childrenMap = new HashMap<>();
        Set<Long> ipjtIds = depts.stream()
                .filter(d -> IPJT_TEAM_NAME.equals(d.getName()))
                .map(Department::getId)
                .collect(Collectors.toSet());

        for (Department d : depts) {
            if (isDescendantOfIpjt(d, ipjtIds)) {
                continue;
            }

            DeptNodeDto dto = new DeptNodeDto();
            dto.setId(d.getId());
            dto.setName(d.getName());
            dto.setParentId(d.getParentId());
            dto.setMemberCount(directCount.getOrDefault(d.getId(), 0L));

            nodeMap.put(d.getId(), dto);
            Department parent = deptById.get(d.getParentId());
            if (isParentPathInconsistent(d, parent)) {
                log.warn(
                        "Detected inconsistent path for department '{}' (id={}) with parent '{}' (id={}). Expected child path to start with '{}'. Linking anyway.",
                        d.getName(), d.getId(), parent != null ? parent.getName() : null,
                        d.getParentId(), parent != null ? parent.getPath() : null
                );
            }

            childrenMap
                    .computeIfAbsent(d.getParentId(), k -> new ArrayList<>())
                    .add(dto);
        }

        childrenMap.values().forEach(list -> list.sort(nodeComparator));

        List<DeptNodeDto> roots = childrenMap.getOrDefault(null, new ArrayList<>());
        roots.sort(nodeComparator);
        for (DeptNodeDto r : roots) {
            accumulate(r, childrenMap, nodeComparator);
        }

        long total = roots.stream()
                .mapToLong(DeptNodeDto::getMemberCount)
                .sum();
        DeptNodeDto totalNode = new DeptNodeDto();
        totalNode.setId(0L);
        totalNode.setName("총원");
        totalNode.setMemberCount(total);
        totalNode.setChildren(roots);

        return List.of(totalNode);
    }

    private List<Department> findDepartmentsExcludingMisplacedGlobalDivision() {
        return deptRepo.findAllByOrderByPathAscSortOrderAscExcluding(GLOBAL_DIVISION_NAME, IPJT_TEAM_NAME);
    }

    private long accumulate(DeptNodeDto node, Map<Long, List<DeptNodeDto>> childrenMap,
                            Comparator<DeptNodeDto> comparator) {
        long sum = node.getMemberCount();
        List<DeptNodeDto> kids = childrenMap.get(node.getId());
        if (kids != null) {
            kids.sort(comparator);
            for (DeptNodeDto c : kids) {
                sum += accumulate(c, childrenMap, comparator);
            }
            node.setChildren(kids);
        }
        node.setMemberCount(sum);
        return sum;
    }

    private boolean isDescendantOfIpjt(Department dept, Set<Long> ipjtIds) {
        if (ipjtIds.isEmpty()) {
            return false;
        }

        String path = stripTrailingSlash(dept.getPath());
        if (path == null || path.isBlank()) {
            return false;
        }

        String[] segments = path.split("/");
        String selfId = String.valueOf(dept.getId());

        for (Long ipjtId : ipjtIds) {
            String ipjtSegment = ipjtId.toString();
            if (!selfId.equals(ipjtSegment)) {
                for (String segment : segments) {
                    if (ipjtSegment.equals(segment)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isParentPathInconsistent(Department child, Department parent) {
        String childPath = child.getPath();
        if (parent == null || childPath == null || childPath.isBlank()) {
            return false;
        }

        String parentPath = parent.getPath();
        if (parentPath == null || parentPath.isBlank()) {
            return false;
        }

        String normalizedParentPath = stripTrailingSlash(parentPath);
        String normalizedChildPath = stripTrailingSlash(childPath);

        int lastSlash = normalizedChildPath.lastIndexOf('/');
        if (lastSlash == -1) {
            return true;
        }

        String childParentPath = normalizedChildPath.substring(0, lastSlash);
        return !normalizedParentPath.equals(childParentPath);
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
package com.daouoffice.daouoffice.service;

import com.daouoffice.daouoffice.dto.DeptNodeDto;
import com.daouoffice.daouoffice.entity.Department;
import com.daouoffice.daouoffice.repository.DeptMemberRepository;
import com.daouoffice.daouoffice.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository deptRepo;

    @Mock
    private DeptMemberRepository memberRepo;

    @InjectMocks
    private DepartmentService service;

    @Test
    void buildTreeWithTotalExcludesMisplacedGlobalDivision() {
        Department strategyOffice = dept(1L, "사업전략실", null, "1", 1);
        Department globalUnderStrategy = dept(2L, "글로벌사업부", 1L, "1/2", 1);
        Department ipjtTeam = dept(3L, "I PJT팀", null, "3", 2);
        when(deptRepo.findAllByOrderByPathAscSortOrderAscExcluding("글로벌사업부", "I PJT팀"))
                .thenReturn(List.of(strategyOffice, globalUnderStrategy, ipjtTeam));

        when(memberRepo.findDirectCounts()).thenReturn(List.of(
                new Object[]{1L, 1L},
                new Object[]{2L, 2L},
                new Object[]{3L, 0L}
        ));

        List<DeptNodeDto> result = service.buildTreeWithTotal();

        assertThat(result).hasSize(1);
        DeptNodeDto total = result.get(0);
        assertThat(total.getName()).isEqualTo("총원");
        assertThat(total.getMemberCount()).isEqualTo(3);

        assertThat(total.getChildren()).hasSize(2);
        DeptNodeDto strategyNode = total.getChildren().get(0);
        DeptNodeDto ipjtNode = total.getChildren().get(1);

        assertThat(strategyNode.getId()).isEqualTo(1L);
        assertThat(strategyNode.getMemberCount()).isEqualTo(3);
        assertThat(strategyNode.getChildren())
                .extracting(DeptNodeDto::getId)
                .containsExactly(2L);

        assertThat(ipjtNode.getId()).isEqualTo(3L);
        assertThat(ipjtNode.getMemberCount()).isZero();
        assertThat(ipjtNode.getChildren()).isEmpty();

        assertThat(findNodeById(total, 4L)).isEmpty();
    }

    @Test
    void buildTreeWithTotalHidesIpjtDescendants() {
        Department strategyOffice = dept(1L, "사업전략실", null, "1", 1);
        Department globalUnderStrategy = dept(2L, "글로벌사업부", 1L, "1/2", 1);
        Department ipjtTeam = dept(3L, "I PJT팀", null, "3", 2);
        Department childOfIpjt = dept(4L, "글로벌사업부", 3L, "3/4", 1);
        Department grandChild = dept(5L, "해외영업팀", 4L, "3/4/5", 1);

        when(deptRepo.findAllByOrderByPathAscSortOrderAscExcluding("글로벌사업부", "I PJT팀"))
                .thenReturn(List.of(strategyOffice, globalUnderStrategy, ipjtTeam, childOfIpjt, grandChild));

        when(memberRepo.findDirectCounts()).thenReturn(List.of(
                new Object[]{1L, 1L},
                new Object[]{2L, 2L},
                new Object[]{3L, 0L},
                new Object[]{4L, 4L},
                new Object[]{5L, 5L}
        ));

        List<DeptNodeDto> result = service.buildTreeWithTotal();

        assertThat(result).hasSize(1);
        DeptNodeDto total = result.get(0);

        assertThat(total.getMemberCount()).isEqualTo(3);

        DeptNodeDto ipjtNode = findNodeById(total, 3L).orElseThrow();
        assertThat(ipjtNode.getChildren()).isEmpty();
        assertThat(findNodeById(total, 4L)).isEmpty();
        assertThat(findNodeById(total, 5L)).isEmpty();
    }

    private Optional<DeptNodeDto> findNodeById(DeptNodeDto root, Long id) {
        if (Objects.equals(root.getId(), id)) {
            return Optional.of(root);
        }
        for (DeptNodeDto child : root.getChildren()) {
            Optional<DeptNodeDto> found = findNodeById(child, id);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    private Department dept(Long id, String name, Long parentId, String path, int sortOrder) {
        Department d = new Department();
        d.setId(id);
        d.setName(name);
        d.setParentId(parentId);
        d.setPath(path);
        d.setSortOrder(sortOrder);
        return d;
    }
}

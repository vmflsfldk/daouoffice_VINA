package com.daouoffice.daouoffice.repository;

import com.daouoffice.daouoffice.dto.UserDto;
import com.daouoffice.daouoffice.entity.Department;
import com.daouoffice.daouoffice.entity.DeptMember;
import com.daouoffice.daouoffice.entity.DeptMemberId;
import com.daouoffice.daouoffice.entity.DomainCode;
import com.daouoffice.daouoffice.entity.User;
import com.daouoffice.daouoffice.entity.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class DeptMemberRepositoryTest {

    @Autowired
    private DeptMemberRepository deptMemberRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("상위 부서를 조회하면 하위 부서 조직원까지 함께 반환한다")
    void findUsersByDepartment_includesDescendants() {
        Department root = persistDepartment("생산기술개발실", null);
        Department child = persistDepartment("공정개발팀", root);
        Department grandChild = persistDepartment("세부개발파트", child);

        User rootUser = persistUser(1L, "루트 사용자");
        User childUser = persistUser(2L, "자식 사용자");
        User grandChildUser = persistUser(3L, "손자 사용자");

        persistMembership(rootUser, root, 1);
        persistMembership(childUser, child, 2);
        persistMembership(grandChildUser, grandChild, 3);

        entityManager.flush();
        entityManager.clear();

        List<UserDto> users = deptMemberRepository.findUsersByDepartment(root.getId());

        assertThat(users)
                .hasSize(3)
                .extracting(UserDto::getUserName)
                .containsExactlyInAnyOrder(
                        "루트 사용자",
                        "자식 사용자",
                        "손자 사용자"
                );
    }

    @Test
    @DisplayName("하위 부서를 조회하면 해당 부서 및 더 하위 부서 조직원만 반환한다")
    void findUsersByDepartment_filtersToSelectedBranch() {
        Department root = persistDepartment("생산기술개발실", null);
        Department child = persistDepartment("공정개발팀", root);
        Department sibling = persistDepartment("신소재개발팀", root);

        User childUser = persistUser(10L, "공정 사용자");
        User grandChildUser = persistUser(11L, "공정 손자 사용자");
        User siblingUser = persistUser(12L, "신소재 사용자");

        Department grandChild = persistDepartment("세부 파트", child);

        persistMembership(childUser, child, 1);
        persistMembership(grandChildUser, grandChild, 2);
        persistMembership(siblingUser, sibling, 3);

        entityManager.flush();
        entityManager.clear();

        List<UserDto> users = deptMemberRepository.findUsersByDepartment(child.getId());

        assertThat(users)
                .extracting(UserDto::getUserName)
                .containsExactlyInAnyOrder(
                        "공정 사용자",
                        "공정 손자 사용자"
                );
    }

    @Test
    @DisplayName("부서 구성원은 상위 부서가 우선 정렬되고 부서 내에서는 직급 순서가 유지된다")
    void findUsersByDepartment_ordersByHierarchyThenRank() {
        Department parent = persistDepartment("본부", null);
        Department child = persistDepartment("팀", parent);

        DomainCode senior = persistDomainCode(100L, "고위", 1);
        DomainCode junior = persistDomainCode(101L, "사원", 10);

        User parentSenior = persistUserWithPosition(21L, "상위-고위", senior);
        User parentJunior = persistUserWithPosition(22L, "상위-사원", junior);
        User childSenior = persistUserWithPosition(23L, "하위-고위", senior);

        persistMembership(parentSenior, parent, 2);
        persistMembership(parentJunior, parent, 1);
        persistMembership(childSenior, child, 1);

        entityManager.flush();
        entityManager.clear();

        List<UserDto> users = deptMemberRepository.findUsersByDepartment(parent.getId());

        assertThat(users)
                .extracting(UserDto::getUserName)
                .containsExactly(
                        "상위-고위",
                        "상위-사원",
                        "하위-고위"
                );
    }

    private Department persistDepartment(String name, Department parent) {
        Department department = new Department();
        department.setName(name);
        if (parent != null) {
            department.setParentId(parent.getId());
        }
        entityManager.persist(department);
        entityManager.flush();
        return department;
    }

    private User persistUser(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        entityManager.persist(user);
        return user;
    }

    private User persistUserWithPosition(Long id, String name, DomainCode position) {
        User user = persistUser(id, name);

        UserProfile profile = new UserProfile();
        profile.setId(user.getId());
        profile.setUser(user);
        if (position != null) {
            profile.setPositionId(position.getId());
            profile.setPositionCode(position);
        }

        entityManager.persist(profile);
        user.setProfile(profile);
        return user;
    }

    private DomainCode persistDomainCode(Long id, String koName, int sortOrder) {
        DomainCode domainCode = new DomainCode();
        domainCode.setId(id);
        domainCode.setKoName(koName);
        domainCode.setSortOrder(sortOrder);
        entityManager.persist(domainCode);
        return domainCode;
    }

    private void persistMembership(User user, Department department, Integer sortOrder) {
        DeptMember member = new DeptMember();
        member.setId(new DeptMemberId(user.getId(), department.getId()));
        member.setUser(user);
        member.setDepartment(department);
        member.setSortOrder(sortOrder);
        member.setType(1);
        entityManager.persist(member);
    }
}

package com.daouoffice.daouoffice.repository;

import com.daouoffice.daouoffice.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAllByOrderByPathAscSortOrderAsc();

    @org.springframework.data.jpa.repository.Query("""
            select d from Department d
            where not (
                d.name = :childName and d.parentId in (
                    select p.id from Department p where p.name = :parentName
                )
            )
            order by d.path asc, d.sortOrder asc
            """)
    List<Department> findAllByOrderByPathAscSortOrderAscExcluding(@org.springframework.data.repository.query.Param("childName") String childName,
                                                                  @org.springframework.data.repository.query.Param("parentName") String parentName);

    List<Department> findAllByNameOrderByIdAsc(String name);
}

package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Integer> {
    @Query(value = "select t from Teacher t where personId=?1")
    Teacher findByPersonId(Integer personId);

    Optional<Teacher> findByPersonNum(String num);

    List<Teacher> findByPersonName(String name);

    @Query(value = "from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ")
    List<Teacher> findTeacherListByNumName(String numName);


    @Query(value = "from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ",
            countQuery = "SELECT count(personId) from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ")
    Page<Teacher> findTeacherPageByNumName(String numName, Pageable pageable);

    Teacher getTeacherByPersonId(Integer personId);
    List<Teacher> findAll();
}

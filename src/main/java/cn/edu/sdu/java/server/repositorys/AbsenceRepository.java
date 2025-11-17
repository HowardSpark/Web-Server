package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Absence;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface AbsenceRepository extends JpaRepository<Absence,Integer> {
    List<Absence> findByStudent(Student student);

    List<Absence> findByTeacher(Teacher teacher);

    Optional<Absence> findById(Integer id);
}

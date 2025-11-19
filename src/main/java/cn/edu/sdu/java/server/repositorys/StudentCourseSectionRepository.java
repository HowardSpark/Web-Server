package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.CourseSection;
import cn.edu.sdu.java.server.models.StudentCourseSection;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentCourseSectionRepository extends JpaRepository<StudentCourseSection,Integer> {
    @Query( value = "from StudentCourseSection where ?1=0 or courseSection.courseSectionId=?1")
    List<StudentCourseSection> findByCourseSection(Integer id);

    @Query(value = "from StudentCourseSection where student.personId=?1 and courseSection.courseSectionId=?2")
    Optional<StudentCourseSection> findByPersonCourseSection(Integer personId, Integer courseSectionId);

    @Query(value = "from StudentCourseSection where student.personId=?1")
    List<StudentCourseSection> findByPerson(Integer personId);

    boolean existsByStudentPersonIdAndCourseSectionCourseSectionId(Integer personId, Integer courseSectionId);

    @Query("from StudentCourseSection scs join fetch scs.courseSection cs join fetch cs.course c join fetch cs.teacher t where scs.student.personId=?1")
    List<StudentCourseSection> findByPersonWithJoin(Integer personId);
    
}

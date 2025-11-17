package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseSectionRepository extends JpaRepository<CourseSection,Integer> {
    @Query(value = "from CourseSection where ?1='' or num like %?1%" )
    List<CourseSection> findCourseSectionListByNum(String num);

    @Query(value = "from CourseSection where ?1='' or teacher.person.num like %?1%")
    List<CourseSection> findCourseSectionListByTeacher(String num);

    @Query(value = "from CourseSection where ?1='' or course.num like %?1%")
    List<CourseSection> findCourseSectionListByCourse(String num);

    @Query(value = "from CourseSection where optional = True")
    List<CourseSection> findAvailableCourseSectionList();

    @Query(value = "from CourseSection where optional = False")
    List<CourseSection> findUnavailableCourseSectionList();
    Optional<CourseSection> findByNum(String num);
}

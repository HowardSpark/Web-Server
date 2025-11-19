package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.CourseSectionRepository;
import cn.edu.sdu.java.server.repositorys.StudentCourseSectionRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentCourseSectionService {
    private final StudentCourseSectionRepository studentCourseSectionRepository;
    private final CourseRepository courseRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final StudentRepository studentRepository;

    public StudentCourseSectionService (StudentCourseSectionRepository studentCourseSectionRepository, CourseRepository courseRepository, CourseSectionRepository courseSectionRepository, StudentRepository studentRepository) {
        this.studentCourseSectionRepository = studentCourseSectionRepository;
        this.courseRepository = courseRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.studentRepository = studentRepository;
    }

    public DataResponse getStudentCourseSectionList(DataRequest dataRequest) {
        List<StudentCourseSection> scsList = studentCourseSectionRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        Course c;
        CourseSection cs;
        Teacher t;
        Student s;
        for(StudentCourseSection scs : scsList) {
            m = new HashMap<>();
            m.put("studentCourseSectionId", scs.getStudentCourseSectionId());
            cs = scs.getCourseSection();
            if(cs != null) {
                m.put("courseSectionId", cs.getCourseSectionId());
                m.put("time", cs.getTime());
                m.put("place", cs.getPlace());
            }
            c = scs.getCourseSection().getCourse();
            if(c != null) {
                m.put("courseId", c.getCourseId());
                m.put("courseName", c.getName());
            }
            t = cs.getTeacher();
            if(t != null) {
                m.put("teacherName", t.getPerson().getName());
            }
            s = scs.getStudent();
            if(s != null) {
                m.put("personId", s.getPersonId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getStudentCourseTable(Integer personId) {
        // 1. 查询学生已选课程记录
        List<StudentCourseSection> scsList = studentCourseSectionRepository.findByPerson(personId);
        List<Map<String, Object>> courseTable = new ArrayList<>();

        for (StudentCourseSection scs : scsList) {
            Map<String, Object> courseInfo = new HashMap<>();
            CourseSection cs = scs.getCourseSection();
            Course course = cs.getCourse();
            Teacher teacher = cs.getTeacher();

            // 2. 封装课程表核心字段
            courseInfo.put("studentCourseSectionId", scs.getStudentCourseSectionId());
            courseInfo.put("courseName", course.getName());
            courseInfo.put("num", cs.getNum());
            courseInfo.put("teacherName", teacher.getPerson().getName());
            courseInfo.put("place", cs.getPlace());
            courseInfo.put("time", cs.getTime()); // 时间编码
            courseInfo.put("status", scs.getStatus()); // 选课状态

            courseTable.add(courseInfo);
        }
        return CommonMethod.getReturnData(courseTable);
    }

}

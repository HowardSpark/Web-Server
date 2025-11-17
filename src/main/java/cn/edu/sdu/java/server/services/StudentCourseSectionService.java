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
        CourseSection cs;
        Student s;
        for(StudentCourseSection scs : scsList) {
            m = new HashMap<>();
            m.put("studentCourseSectionId", scs.getStudentCourseSectionId());
            cs = scs.getCourseSection();
            if(cs != null) {
                m.put("courseSectionId", cs.getCourseSectionId());
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
//    public DataResponse getStudentCourseSectionList(DataRequest dataRequest) {
//        // 1. 获取当前学生ID（从登录态/请求参数获取）
//        Integer personId = CommonMethod.getPersonId();
//
//        // 2. 查询该学生已选的课程段（关联课程信息）
//        List<StudentCourseSection> scsList = studentCourseSectionRepository.findByPerson(personId);
//        List<Map<String, Object>> dataList = new ArrayList<>();
//
//        for (StudentCourseSection scs : scsList) {
//            Map<String, Object> courseMap = new HashMap<>();
//            CourseSection cs = scs.getCourseSection();
//            Student student = scs.getStudent();
//            Course course = cs.getCourse();
//
//            // 🔥 核心：直接返回原始time字段，不解析weekDay和period
//            Integer time = cs.getTime(); // 原始time值（如"101"、"304"）
//            courseMap.put("time", time); // 仅返回原始time
//
//            // 组装其他必要字段（前端显示用）
//            courseMap.put("studentCourseSectionId", scs.getStudentCourseSectionId());
//            courseMap.put("courseSectionId", cs.getCourseSectionId());
//            courseMap.put("courseName", course.getName());
//            courseMap.put("place", cs.getPlace()); // 教室
//            courseMap.put("studentName", student.getPerson().getName());
//
//            dataList.add(courseMap);
//        }
//
//        return CommonMethod.getReturnData(dataList);
//    }

}

package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * StudentCourseSection 学生选课信息
 * Integer StudentCourseSectionId 选课记录ID，主键
 * Student student 关联学生对象
 * CourseSection courseSection 关联课次对象
 * Boolean status 选课状态
 */

@Getter
@Setter
@Entity
@Table(name = "student_course_section")
public class StudentCourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentCourseSectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "person_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "course_section_id")
    private CourseSection courseSection;

    private Boolean status;

//    private LocalDateTime selectedTime;
}
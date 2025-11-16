package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
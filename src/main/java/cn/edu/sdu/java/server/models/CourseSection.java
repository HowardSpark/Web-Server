package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * CourseSection 课次
 * Integer courseSectionId 课次ID，主键
 * String num 课序号
 * String place 上课地点
 * Boolean optional 是否可选
 * Integer time 上课时间代码
 * Teacher teacher 关联上课教师
 * Course course 关联课程
 */

@Getter
@Setter
@Entity
@Table(	name = "course_section",
        uniqueConstraints = {
        })
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseSectionId;
    @NotBlank
    @Size(max = 20)
    private String num;

    @Size(max = 100)
    private String place;

    private Boolean optional;

    private Integer time;

    @ManyToOne(optional = true)
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Teacher teacher;


    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

}

package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

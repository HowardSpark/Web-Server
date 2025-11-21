package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Teacher 教师信息
 * Integer teacherId 志愿记录ID，主键
 * Person person 关联人员对象
 * String title 教师职称
 * String degree 学位
 */

@Getter
@Setter
@Entity
@Table(	name = "teacher",
        uniqueConstraints = {
        })
public class Teacher {
    @Id
    private Integer personId;

    @OneToOne
    @JoinColumn(name="personId")
    @JsonIgnore
    private Person person;

    @Size(max = 20)
    private String title;

    @Size(max = 10)
    private String degree;

}

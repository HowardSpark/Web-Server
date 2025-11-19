package cn.edu.sdu.java.server.models;


/*
 * Course 课程表实体类  保存课程的的基本信息信息，
 * Integer courseId 人员表 course 主键 course_id
 * String num 课程编号
 * String name 课程名称
 * Integer credit 学分
 * Course preCourse 前序课程 pre_course_id 关联前序课程的主键 course_id
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(	name = "absence",
        uniqueConstraints = {
        })
public class Absence  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer absenceId;
    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;
    @Size(max = 20)
    private String beginTime;
    @Size(max = 20)
    private String endTime;
    @Size(max = 20)
    private String absenceType;
    @Size(max = 100)
    private String absenceReason;

    private String result;

    private boolean isDeletedStudent;

    private boolean isDeletedTeacher;

    private boolean isDeletedAdmin;

    public boolean getIsDeletedStudent() {
        return isDeletedStudent;
    }
    public void setIsDeletedStudent(boolean isDeletedStudent) {
        this.isDeletedStudent = isDeletedStudent;
    }
    public boolean getIsDeletedTeacher() {
        return isDeletedTeacher;
    }
    public void setIsDeletedTeacher(boolean isDeletedTeacher) {
        this.isDeletedTeacher = isDeletedTeacher;
    }
    public boolean getIsDeletedAdmin() {
        return isDeletedAdmin;
    }
    public void setIsDeletedAdmin(boolean isDeletedAdmin) {
        this.isDeletedAdmin = isDeletedAdmin;
    }
}

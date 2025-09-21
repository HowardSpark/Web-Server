package cn.edu.sdu.java.server.models;

import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Honor 荣誉表实体类 保存学生荣誉的基本信息
 * Integer honorId 荣誉表主键 honor_id
 * Student student 关联学生 student_id 关联学生的主键 student_id
 * String honorName 荣誉名称
 * String honorLevel 荣誉等级（国家级/省级/市级/校级/院级）
 * LocalDate awardTime 获得时间
 * String awardUnit 授予单位
 * String description 荣誉描述
 */
@Getter
@Setter
@Entity
@Table(name = "honor",
        uniqueConstraints = {})
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorId; // 荣誉记录唯一标识


    @ManyToOne
    @JoinColumn(name = "personId") // 与学生表关联的外键，同Score保持一致
    private Student student; // 关联学生对象

    private String honorName; // 荣誉名称（如"优秀学生标兵"、"数学竞赛一等奖"）

    private String honorLevel; // 荣誉等级（建议存储：national/provincial/municipal/school/college）

    private LocalDate awardTime; // 获得时间（使用LocalDate类型存储日期）

    private String awardUnit; // 授予单位（如"XX大学"、"XX省教育厅"）

    private String description; // 荣誉描述（可选，如获奖事由、竞赛详情等）

    public Integer getHonorId() {
        return honorId;
    }

    public void setHonorId(Integer honorId) {
        this.honorId = honorId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getHonorName() {
        return honorName;
    }

    public void setHonorName(String honorName) {
        this.honorName = honorName;
    }

    public String getHonorLevel() {
        return honorLevel;
    }

    public void setHonorLevel(String honorLevel) {
        this.honorLevel = honorLevel;
    }

    public LocalDate getAwardTime() {
        return awardTime;
    }

    public void setAwardTime(LocalDate awardTime) {
        this.awardTime = awardTime;
    }

    public String getAwardUnit() {
        return awardUnit;
    }

    public void setAwardUnit(String awardUnit) {
        this.awardUnit = awardUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

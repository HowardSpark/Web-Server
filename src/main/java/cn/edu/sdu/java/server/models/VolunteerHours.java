package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * VolunteerHours志愿者时长记录表实体类
 * Integer volunteerId 志愿记录ID，主键
 * Student student 关联学生对象
 * String activityName 志愿活动名称
 * BigDecimal hours 志愿时长(小时)
 * LocalDate activityDate 活动日期
 * String organization 组织单位
 * String description 活动描述
 * String status 状态：已完成、审核中、已取消
 * String createTime 创建时间
 * Integer creatorId 创建者ID
 */
@Getter
@Setter
@Entity
@Table(name = "volunteer_hours")
public class VolunteerHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer volunteerId;

    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonIgnore
    private Student student;

    @NotBlank
    @Size(max = 100)
    private String activityName;

    @NotNull
    @Column(precision = 4, scale = 1)
    private BigDecimal hours;

    @NotNull
    private LocalDate activityDate;

    @Size(max = 100)
    private String organization;

    @Size(max = 1000)
    private String description;

    @Size(max = 10)
    private String status = "已完成";

    @Size(max = 20)
    private String createTime;

    private Integer creatorId;
} 
package cn.edu.sdu.java.server.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(	name = "notification",
        uniqueConstraints = {
        })
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticeId;
    @Size(max = 20)
    @Column(nullable = false) // 非空约束
    private String num; // 通知编号

    @Size(max = 50)
    @Column(nullable = false)
    private String title; // 通知标题

    @Column(nullable = false)
    private LocalDateTime releaseTime; // 发布时间（后端存储为Date类型）

    @Column(columnDefinition = "TEXT") // 内容可能较长，使用TEXT类型
    private String content; // 通知内容


}

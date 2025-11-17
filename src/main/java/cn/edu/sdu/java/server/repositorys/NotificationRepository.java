package cn.edu.sdu.java.server.repositorys;
import cn.edu.sdu.java.server.models.Notification;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
public interface NotificationRepository extends JpaRepository<Notification,Integer>{
    // 根据通知ID查询通知
    Optional<Notification> findByNoticeId(Integer noticeId);

    // 根据通知编号查询通知
    Optional<Notification> findByNum(String num);

    // 根据通知标题查询通知列表
    List<Notification> findByTitle(String title);

    // 根据编号或标题模糊查询通知列表（支持空字符匹配所有）
    @Query(value = "from Notification where ?1='' or num like %?1% or title like %?1% ")
    List<Notification> findNotificationListByNumTitle(String numTitle);

    // 分页查询：根据编号或标题模糊查询（支持空字符匹配所有），包含分页总数查询
    @Query(value = "from Notification where ?1='' or num like %?1% or title like %?1% ",
            countQuery = "SELECT count(noticeId) from Notification where ?1='' or num like %?1% or title like %?1% ")
    Page<Notification> findNotificationPageByNumTitle(String numTitle, Pageable pageable);
}


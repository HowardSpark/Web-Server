package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.VolunteerHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * VolunteerHoursRepository 志愿者时长数据操作接口，继承JpaRepository接口
 */
@Repository
public interface VolunteerHoursRepository extends JpaRepository<VolunteerHours, Integer> {

    /**
     * 根据学生姓名模糊查询志愿者时长记录
     */
    @Query("select vh from VolunteerHours vh where vh.student.person.name like %?1%")
    List<VolunteerHours> findVolunteerHoursListByName(String name);

    /**
     * 分页查询志愿者时长记录（根据姓名模糊查询）
     */
    @Query("select vh from VolunteerHours vh where vh.student.person.name like %?1%")
    Page<VolunteerHours> findVolunteerHoursPageByName(String name, Pageable pageable);

    /**
     * 根据学生ID查询该学生的所有志愿时长记录
     */
    @Query("select vh from VolunteerHours vh where vh.student.personId = ?1")
    List<VolunteerHours> findByStudentPersonId(Integer personId);

    /**
     * 计算某个学生的总志愿时长
     */
    @Query("select sum(vh.hours) from VolunteerHours vh where vh.student.personId = ?1 and vh.status = '已完成'")
    BigDecimal getTotalHoursByStudentId(Integer personId);

    /**
     * 根据学生姓名和学号模糊查询
     */
    @Query("select vh from VolunteerHours vh where (vh.student.person.name like %?1% or vh.student.person.num like %?1%)")
    List<VolunteerHours> findVolunteerHoursListByNameOrNum(String nameOrNum);

    /**
     * 分页查询（根据姓名和学号模糊查询）
     */
    @Query("select vh from VolunteerHours vh where (vh.student.person.name like %?1% or vh.student.person.num like %?1%)")
    Page<VolunteerHours> findVolunteerHoursPageByNameOrNum(String nameOrNum, Pageable pageable);
} 
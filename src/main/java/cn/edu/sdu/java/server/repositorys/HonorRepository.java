package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Honor 数据操作接口，实现荣誉信息的查询操作
 */
@Repository
public interface HonorRepository extends JpaRepository<Honor, Integer> {

    // 根据学生ID查询其所有荣誉
    List<Honor> findByStudentPersonId(Integer personId);

    // 修改：使用命名参数和更明确的查询条件
    @Query("SELECT h FROM Honor h WHERE " +
            "(:personId = 0 OR h.student.personId = :personId) AND " +
            "(:honorLevel IS NULL OR :honorLevel = '' OR h.honorLevel = :honorLevel)")
    List<Honor> findByStudentAndLevel(@Param("personId") Integer personId,
                                      @Param("honorLevel") String honorLevel);

    // 根据学生ID和荣誉名称模糊查询
    @Query("SELECT h FROM Honor h WHERE h.student.personId = :personId AND " +
            "(:honorName IS NULL OR h.honorName LIKE %:honorName%)")
    List<Honor> findByStudentAndName(@Param("personId") Integer personId,
                                     @Param("honorName") String honorName);

    // 统计指定学生的荣誉数量（按等级分组）
    @Query("SELECT h.student.personId, h.honorLevel, COUNT(h.honorId) FROM Honor h " +
            "WHERE h.student.personId IN :personIds GROUP BY h.student.personId, h.honorLevel")
    List<Object[]> getStudentHonorStatistics(@Param("personIds") List<Integer> personIds);

    // 新增：直接查询所有荣誉记录（用于调试）
    @Query("SELECT h FROM Honor h")
    List<Honor> findAllHonors();
}
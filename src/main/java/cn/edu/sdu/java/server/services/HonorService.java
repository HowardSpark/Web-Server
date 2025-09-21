package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.HonorRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HonorService {
    private final HonorRepository honorRepository;
    private final StudentRepository studentRepository;


    public HonorService(HonorRepository honorRepository, StudentRepository studentRepository) {
        this.honorRepository = honorRepository;
        this.studentRepository = studentRepository;
        System.out.println("=== HonorService 构造函数被调用 ===");
    }

    /**
     * 获取学生下拉选项列表（复用成绩系统的学生数据）
     */
    public OptionItemList getStudentItemOptionList( DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    /**
     * 获取荣誉等级下拉选项列表（国家级/省级/市级/校级/院级）
     */
    public OptionItemList getHonorLevelOptionList(DataRequest dataRequest) {
        List<OptionItem> itemList = new ArrayList<>();
        // 手动添加荣誉等级选项（也可从数据库配置表查询）
        itemList.add(new OptionItem(1, "national", "国家级"));
        itemList.add(new OptionItem(2, "provincial", "省级"));
        itemList.add(new OptionItem(3, "municipal", "市级"));
        itemList.add(new OptionItem(4, "school", "校级"));
        itemList.add(new OptionItem(5, "college", "院级"));
        return new OptionItemList(0, itemList);
    }

    /**
     * 查询荣誉列表（按学生ID和荣誉等级筛选）
     */
    public DataResponse getHonorList(DataRequest dataRequest) {

        System.out.println("=== 后端 HonorService.getHonorList 开始 ===");

        Integer personId = dataRequest.getInteger("personId");
        String honorLevel = dataRequest.getString("honorLevel");

        System.out.println("接收到的参数 - personId: " + personId + ", honorLevel: " + honorLevel);

        // 参数处理
        if (personId == null) {
            personId = 0;
        }
        if (honorLevel != null && honorLevel.trim().isEmpty()) {
            honorLevel = null;
        }

        System.out.println("处理后的参数 - personId: " + personId + ", honorLevel: " + honorLevel);

        // 执行查询
        List<Honor> hList = honorRepository.findByStudentAndLevel(personId, honorLevel);
        System.out.println("数据库查询结果数量: " + hList.size());

        // 检查查询结果详情
        for (int i = 0; i < hList.size(); i++) {
            Honor h = hList.get(i);
            System.out.println("记录 " + i + ": " +
                    "honorId=" + h.getHonorId() + ", " +
                    "studentId=" + (h.getStudent() != null ? h.getStudent().getPersonId() : "null") + ", " +
                    "studentName=" + (h.getStudent() != null && h.getStudent().getPerson() != null ? h.getStudent().getPerson().getName() : "null") + ", " +
                    "honorName=" + h.getHonorName() + ", " +
                    "honorLevel=" + h.getHonorLevel());
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Honor h : hList) {
            m = new HashMap<>();
            m.put("honorId", h.getHonorId());
            m.put("personId", h.getStudent().getPersonId());
            m.put("studentNum", h.getStudent().getPerson().getNum());
            m.put("studentName", h.getStudent().getPerson().getName());
            m.put("className", h.getStudent().getClassName());
            m.put("honorName", h.getHonorName());
            m.put("honorLevel", h.getHonorLevel());
            m.put("awardTime", h.getAwardTime() != null ? h.getAwardTime().toString() : "");
            m.put("awardUnit", h.getAwardUnit() != null ? h.getAwardUnit() : "");
            m.put("description", h.getDescription() != null ? h.getDescription() : "");
            dataList.add(m);
        }

        System.out.println("最终返回数据条数: " + dataList.size());
        System.out.println("=== 后端 HonorService.getHonorList 结束 ===");

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 保存荣誉信息（新增或修改）
     */
    public DataResponse honorSave(DataRequest dataRequest) {
        Integer honorId = dataRequest.getInteger("honorId");
        Integer personId = dataRequest.getInteger("personId");
        String honorName = dataRequest.getString("honorName");
        String honorLevel = dataRequest.getString("honorLevel");
        String awardTimeStr = dataRequest.getString("awardTime");
        String awardUnit = dataRequest.getString("awardUnit");
        String description = dataRequest.getString("description");

        Optional<Honor> op;
        Honor h = null;
        // 编辑已有荣誉
        if (honorId != null) {
            op = honorRepository.findById(honorId);
            if (op.isPresent()) {
                h = op.get();
            }
        }
        // 新增荣誉
        if (h == null) {
            h = new Honor();
            Optional<Student> studentOp = studentRepository.findById(personId);
            if (studentOp.isEmpty()) {
                return CommonMethod.getReturnMessageError("学生不存在，无法添加荣誉");
            }
            h.setStudent(studentOp.get());
        }
        // 设置荣誉信息
        h.setHonorName(honorName);
        h.setHonorLevel(honorLevel);
        h.setAwardTime(LocalDate.parse(awardTimeStr));  // 转换为LocalDate
        h.setAwardUnit(awardUnit);
        h.setDescription(description);

        honorRepository.save(h);
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 删除荣誉信息
     */
    public DataResponse honorDelete(DataRequest dataRequest) {
        Integer honorId = dataRequest.getInteger("honorId");
        if (honorId != null) {
            Optional<Honor> op = honorRepository.findById(honorId);
            op.ifPresent(honor -> honorRepository.delete(honor));
        }
        return CommonMethod.getReturnMessageOK();
    }
    /**
     * 测试方法：直接查询所有荣誉记录
     */
    public DataResponse testAllHonors(DataRequest dataRequest) {
        System.out.println("=== 测试所有荣誉记录 ===");

        // 直接查询所有记录
        List<Honor> allHonors = honorRepository.findAll();
        System.out.println("直接查询所有记录数量: " + allHonors.size());

        for (Honor h : allHonors) {
            System.out.println("荣誉记录: " +
                    "honorId=" + h.getHonorId() + ", " +
                    "studentId=" + (h.getStudent() != null ? h.getStudent().getPersonId() : "null") + ", " +
                    "honorName=" + h.getHonorName() + ", " +
                    "honorLevel=" + h.getHonorLevel());
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Honor h : allHonors) {
            Map<String, Object> m = new HashMap<>();
            m.put("honorId", h.getHonorId());
            m.put("personId", h.getStudent().getPersonId());
            m.put("studentName", h.getStudent().getPerson().getName());
            m.put("honorName", h.getHonorName());
            m.put("honorLevel", h.getHonorLevel());
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }
    // 在 HonorService 中添加这个方法用于调试
    public DataResponse debugAllHonors(DataRequest dataRequest) {
        System.out.println("=== 调试：直接查询所有荣誉记录 ===");

        // 使用新的调试方法
        List<Honor> allHonors = honorRepository.findAllHonors();
        System.out.println("直接查询所有记录数量: " + allHonors.size());

        for (Honor h : allHonors) {
            System.out.println("调试记录: honorId=" + h.getHonorId() +
                    ", studentId=" + (h.getStudent() != null ? h.getStudent().getPersonId() : "null") +
                    ", honorName=" + h.getHonorName() +
                    ", honorLevel=" + h.getHonorLevel());
        }

        // 转换为前端需要的格式
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Honor h : allHonors) {
            Map<String, Object> m = new HashMap<>();
            m.put("honorId", h.getHonorId());
            m.put("personId", h.getStudent().getPersonId());
            m.put("studentNum", h.getStudent().getPerson().getNum());
            m.put("studentName", h.getStudent().getPerson().getName());
            m.put("className", h.getStudent().getClassName());
            m.put("honorName", h.getHonorName());
            m.put("honorLevel", h.getHonorLevel());
            m.put("awardTime", h.getAwardTime() != null ? h.getAwardTime().toString() : "");
            m.put("awardUnit", h.getAwardUnit() != null ? h.getAwardUnit() : "");
            m.put("description", h.getDescription() != null ? h.getDescription() : "");
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }


}
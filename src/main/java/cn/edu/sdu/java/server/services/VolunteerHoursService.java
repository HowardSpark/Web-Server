package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.VolunteerHours;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.VolunteerHoursRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VolunteerHoursService {

    private final VolunteerHoursRepository volunteerHoursRepository;
    private final StudentRepository studentRepository;
    private final SystemService systemService;

    public VolunteerHoursService(VolunteerHoursRepository volunteerHoursRepository, 
                                StudentRepository studentRepository,
                                SystemService systemService) {
        this.volunteerHoursRepository = volunteerHoursRepository;
        this.studentRepository = studentRepository;
        this.systemService = systemService;
    }

    /**
     * 将VolunteerHours对象转换为Map
     */
    public Map<String, Object> getMapFromVolunteerHours(VolunteerHours vh) {
        Map<String, Object> m = new HashMap<>();
        if (vh == null) return m;

        Student student = vh.getStudent();
        if (student != null && student.getPerson() != null) {
            m.put("personId", student.getPersonId());
            m.put("studentName", student.getPerson().getName());
            m.put("studentNum", student.getPerson().getNum());
            m.put("className", student.getClassName());
            m.put("major", student.getMajor());
            
            // 计算总时长和剩余时长
            BigDecimal totalHours = volunteerHoursRepository.getTotalHoursByStudentId(student.getPersonId());
            if (totalHours == null) totalHours = BigDecimal.ZERO;
            BigDecimal remainingHours = BigDecimal.valueOf(32).subtract(totalHours);
            if (remainingHours.compareTo(BigDecimal.ZERO) < 0) {
                remainingHours = BigDecimal.ZERO;
            }
            
            m.put("totalHours", totalHours);
            m.put("remainingHours", remainingHours);
            m.put("isCompleted", totalHours.compareTo(BigDecimal.valueOf(32)) >= 0);
        }

        m.put("volunteerId", vh.getVolunteerId());
        m.put("activityName", vh.getActivityName());
        m.put("hours", vh.getHours());
        m.put("activityDate", vh.getActivityDate() != null ? vh.getActivityDate().toString() : "");
        m.put("organization", vh.getOrganization());
        m.put("description", vh.getDescription());
        m.put("status", vh.getStatus());
        m.put("createTime", vh.getCreateTime());

        return m;
    }

    /**
     * 获取志愿者时长列表
     */
    public List<Map<String, Object>> getVolunteerHoursMapList(String nameOrNum) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<VolunteerHours> vhList;
        
        if (nameOrNum == null || nameOrNum.trim().isEmpty()) {
            vhList = volunteerHoursRepository.findAll();
        } else {
            vhList = volunteerHoursRepository.findVolunteerHoursListByNameOrNum(nameOrNum.trim());
        }

        if (vhList != null && !vhList.isEmpty()) {
            for (VolunteerHours vh : vhList) {
                dataList.add(getMapFromVolunteerHours(vh));
            }
        }
        return dataList;
    }

    /**
     * 获取志愿者时长列表（用于前端）
     */
    public DataResponse getVolunteerHoursList(DataRequest dataRequest) {
        String nameOrNum = dataRequest.getString("nameOrNum");
        List<Map<String, Object>> dataList = getVolunteerHoursMapList(nameOrNum);
        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 分页获取志愿者时长数据
     */
    public DataResponse getVolunteerHoursPageData(DataRequest dataRequest) {
        String nameOrNum = dataRequest.getString("nameOrNum");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        Pageable pageable = PageRequest.of(cPage, size);
        Page<VolunteerHours> page;
        
        if (nameOrNum == null || nameOrNum.trim().isEmpty()) {
            page = volunteerHoursRepository.findAll(pageable);
        } else {
            page = volunteerHoursRepository.findVolunteerHoursPageByNameOrNum(nameOrNum.trim(), pageable);
        }

        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<VolunteerHours> list = page.getContent();
            if (!list.isEmpty()) {
                for (VolunteerHours vh : list) {
                    dataList.add(getMapFromVolunteerHours(vh));
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }

    /**
     * 获取志愿者时长详细信息
     */
    public DataResponse getVolunteerHoursInfo(DataRequest dataRequest) {
        Integer volunteerId = dataRequest.getInteger("volunteerId");
        VolunteerHours vh = null;
        Optional<VolunteerHours> op;
        
        if (volunteerId != null) {
            op = volunteerHoursRepository.findById(volunteerId);
            if (op.isPresent()) {
                vh = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromVolunteerHours(vh));
    }

    /**
     * 保存志愿者时长记录
     */
    public DataResponse volunteerHoursSave(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer volunteerId = CommonMethod.getInteger(form, "volunteerId");
        Integer personId = CommonMethod.getInteger(form, "personId");
        
        VolunteerHours vh = null;
        Optional<VolunteerHours> op;
        boolean isNew = false;
        
        if (volunteerId != null) {
            op = volunteerHoursRepository.findById(volunteerId);
            if (op.isPresent()) {
                vh = op.get();
            }
        }
        
        if (vh == null) {
            vh = new VolunteerHours();
            isNew = true;
            if (personId != null) {
                Optional<Student> studentOp = studentRepository.findById(personId);
                if (studentOp.isPresent()) {
                    vh.setStudent(studentOp.get());
                } else {
                    return CommonMethod.getReturnMessageError("学生不存在！");
                }
            } else {
                return CommonMethod.getReturnMessageError("必须选择学生！");
            }
            vh.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            vh.setCreatorId(CommonMethod.getPersonId());
        }

        vh.setActivityName(CommonMethod.getString(form, "activityName"));
        
        // 处理时长
        String hoursStr = CommonMethod.getString(form, "hours");
        if (hoursStr != null && !hoursStr.trim().isEmpty()) {
            try {
                vh.setHours(new BigDecimal(hoursStr));
            } catch (NumberFormatException e) {
                return CommonMethod.getReturnMessageError("时长格式不正确！");
            }
        }
        
        // 处理日期
        String dateStr = CommonMethod.getString(form, "activityDate");
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                vh.setActivityDate(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("日期格式不正确！");
            }
        }
        
        vh.setOrganization(CommonMethod.getString(form, "organization"));
        vh.setDescription(CommonMethod.getString(form, "description"));
        vh.setStatus(CommonMethod.getString(form, "status"));

        volunteerHoursRepository.save(vh);
        systemService.modifyLog(vh, isNew);
        return CommonMethod.getReturnData(vh.getVolunteerId());
    }

    /**
     * 删除志愿者时长记录
     */
    public DataResponse volunteerHoursDelete(DataRequest dataRequest) {
        Integer volunteerId = dataRequest.getInteger("volunteerId");
        VolunteerHours vh = null;
        Optional<VolunteerHours> op;
        
        if (volunteerId != null && volunteerId > 0) {
            op = volunteerHoursRepository.findById(volunteerId);
            if (op.isPresent()) {
                vh = op.get();
                volunteerHoursRepository.delete(vh);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 获取学生志愿时长统计信息
     */
    public DataResponse getStudentVolunteerSummary(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if (personId == null) {
            return CommonMethod.getReturnMessageError("学生ID不能为空！");
        }

        Optional<Student> studentOp = studentRepository.findById(personId);
        if (!studentOp.isPresent()) {
            return CommonMethod.getReturnMessageError("学生不存在！");
        }

        Student student = studentOp.get();
        List<VolunteerHours> vhList = volunteerHoursRepository.findByStudentPersonId(personId);
        BigDecimal totalHours = volunteerHoursRepository.getTotalHoursByStudentId(personId);
        if (totalHours == null) totalHours = BigDecimal.ZERO;

        Map<String, Object> summary = new HashMap<>();
        summary.put("studentName", student.getPerson().getName());
        summary.put("studentNum", student.getPerson().getNum());
        summary.put("className", student.getClassName());
        summary.put("totalHours", totalHours);
        summary.put("remainingHours", BigDecimal.valueOf(32).subtract(totalHours).max(BigDecimal.ZERO));
        summary.put("isCompleted", totalHours.compareTo(BigDecimal.valueOf(32)) >= 0);
        summary.put("recordCount", vhList.size());

        List<Map<String, Object>> records = new ArrayList<>();
        for (VolunteerHours vh : vhList) {
            records.add(getMapFromVolunteerHours(vh));
        }
        summary.put("records", records);

        return CommonMethod.getReturnData(summary);
    }
} 
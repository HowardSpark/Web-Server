package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.VolunteerHoursService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * VolunteerHoursController 志愿者时长管理Web服务
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/volunteer")
public class VolunteerHoursController {

    private final VolunteerHoursService volunteerHoursService;

    public VolunteerHoursController(VolunteerHoursService volunteerHoursService) {
        this.volunteerHoursService = volunteerHoursService;
    }

    /**
     * 获取志愿者时长列表
     * @param dataRequest nameOrNum 姓名或学号（模糊查询）
     * @return 志愿者时长列表
     */
    @PostMapping("/getVolunteerHoursList")
    public DataResponse getVolunteerHoursList(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerHoursService.getVolunteerHoursList(dataRequest);
    }

    /**
     * 分页获取志愿者时长数据
     * @param dataRequest nameOrNum 姓名或学号（模糊查询），currentPage 当前页
     * @return 分页数据
     */
    @PostMapping("/getVolunteerHoursPageData")
    public DataResponse getVolunteerHoursPageData(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerHoursService.getVolunteerHoursPageData(dataRequest);
    }

    /**
     * 获取志愿者时长详细信息
     * @param dataRequest volunteerId 志愿记录ID
     * @return 志愿者时长详细信息
     */
    @PostMapping("/getVolunteerHoursInfo")
    public DataResponse getVolunteerHoursInfo(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerHoursService.getVolunteerHoursInfo(dataRequest);
    }

    /**
     * 保存志愿者时长记录
     * @param dataRequest form 表单数据
     * @return 保存结果
     */
    @PostMapping("/volunteerHoursSave")
    public DataResponse volunteerHoursSave(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerHoursService.volunteerHoursSave(dataRequest);
    }

    /**
     * 删除志愿者时长记录
     * @param dataRequest volunteerId 志愿记录ID
     * @return 删除结果
     */
    @PostMapping("/volunteerHoursDelete")
    public DataResponse volunteerHoursDelete(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerHoursService.volunteerHoursDelete(dataRequest);
    }

    /**
     * 获取学生志愿时长统计信息
     * @param dataRequest personId 学生ID
     * @return 统计信息
     */
    @PostMapping("/getStudentVolunteerSummary")
    public DataResponse getStudentVolunteerSummary(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerHoursService.getStudentVolunteerSummary(dataRequest);
    }
} 
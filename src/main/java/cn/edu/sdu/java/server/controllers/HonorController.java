package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.HonorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/honor")  // 路由前缀改为荣誉管理专用
public class HonorController {
    private final HonorService honorService;
    // 注入日志对象（必须添加，否则 log.info 会报错）
    private static final Logger log = LoggerFactory.getLogger(HonorController.class);

    public HonorController(HonorService honorService) {
        this.honorService = honorService;
    }


    /**
     * 获取学生下拉选项列表（复用成绩系统的学生数据接口）
     */
    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.getStudentItemOptionList(dataRequest);
    }

    /**
     * 获取荣誉等级下拉选项列表（替代课程下拉列表接口）
     */
    @PostMapping("/getHonorLevelOptionList")
    public OptionItemList getHonorLevelOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.getHonorLevelOptionList(dataRequest);
    }

    /**
     * 查询荣誉列表（替代成绩列表查询接口）
     */
    @PostMapping("/getHonorList")
    public DataResponse getHonorList(@RequestBody DataRequest request) {

        return honorService.getHonorList(request);
    }

    /**
     * 保存荣誉信息（新增/修改，替代成绩保存接口）
     */
    @PostMapping("/honorSave")
    public DataResponse honorSave(@Valid @RequestBody DataRequest dataRequest) {
        // 新增：打印接收的参数，确认是否包含前端提交的字段
        log.info("honorSave 接收的参数：{}", dataRequest);
        return honorService.honorSave(dataRequest);
    }

    /**
     * 删除荣誉信息（替代成绩删除接口）
     */
    @PostMapping("/honorDelete")
    public DataResponse honorDelete(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.honorDelete(dataRequest);
    }
    /**
     * 测试接口：直接返回所有荣誉数据
     */
    @PostMapping("/testAllHonors")
    public DataResponse testAllHonors(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.testAllHonors(dataRequest);
    }
    // 在 HonorController 中添加
    @PostMapping("/debugAllHonors")
    public DataResponse debugAllHonors(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.debugAllHonors(dataRequest);
    }
}
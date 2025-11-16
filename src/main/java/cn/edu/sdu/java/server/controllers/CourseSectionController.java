package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.CourseSectionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courseSection")
public class CourseSectionController {
    private final CourseSectionService courseSectionService;

    public CourseSectionController (CourseSectionService courseSectionService) {
        this.courseSectionService = courseSectionService;
    }

    @PostMapping("/getCourseSectionList")
    public DataResponse getCourseSectionList(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.getCourseSectionList(dataRequest);
    }
    @PostMapping("/courseSectionSave")
    public DataResponse courseSectionSave(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.courseSectionSave(dataRequest);
    }
    @PostMapping("/courseSectionDelete")
    public DataResponse courseSectionDelete(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.courseSectionDelete(dataRequest);
    }
    @PostMapping("/courseSectionPublish")
    public DataResponse courseSectionPublish(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.courseSectionPublish(dataRequest);
    }
    @PostMapping("/courseSectionSelect")
    public DataResponse courseSectionSelect(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.courseSectionSelect(dataRequest);
    }
    @PostMapping("/courseSectionUnselect")
    public DataResponse courseSectionUnselect(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.courseSectionUnselect(dataRequest);
    }
    @PostMapping("/getAvailableCourseSectionList")
    public DataResponse getAvailableCourseSectionList(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.getAvailableCourseSectionList(dataRequest);
    }
    @PostMapping("/getUnavailableCourseSectionList")
    public DataResponse getUnavailableCourseSectionList(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.getUnavailableCourseSectionList(dataRequest);
    }
    @PostMapping("/getTeacherItemOptionList")
    public OptionItemList getTeacherItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.getTeacherItemOptionList(dataRequest);
    }
    @PostMapping("/getTimeItemOptionList")
    public OptionItemList getTimeItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return courseSectionService.getTimeItemOptionList(dataRequest);
    }
}

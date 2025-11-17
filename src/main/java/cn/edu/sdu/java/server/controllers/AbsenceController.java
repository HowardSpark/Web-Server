package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.AbsenceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/absence")

public class AbsenceController {
    private final AbsenceService absenceService;
    public AbsenceController(AbsenceService absenceService) {this.absenceService = absenceService;}
    @PostMapping("/getAbsenceList")
    public DataResponse getAbsenceDataStudent() {
        return absenceService.getAbsenceList();
    }
    @PostMapping("/absenceSave")
    public DataResponse courseSave(@Valid @RequestBody DataRequest dataRequest) {
        return absenceService.absenceSave(dataRequest);
    }
    @PostMapping("/absenceDelete")
    public DataResponse absenceDelete(@Valid @RequestBody DataRequest dataRequest) {
        return absenceService.absenceDelete(dataRequest);
    }
    @PostMapping("/getTeacherItemOptionList")
    public OptionItemList getTeacherItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return absenceService.getTeacherItemOptionList(dataRequest);
    }
    @PostMapping("/setResult")
    public DataResponse setResult(@Valid @RequestBody DataRequest dataRequest) {
        return absenceService.setResult(dataRequest);
    }
}
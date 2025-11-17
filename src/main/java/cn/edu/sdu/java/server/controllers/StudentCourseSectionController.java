package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentCourseSectionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentCourseSection")
public class StudentCourseSectionController {
    private final StudentCourseSectionService studentCourseSectionService;

    public StudentCourseSectionController(StudentCourseSectionService studentCourseSectionService) {
        this.studentCourseSectionService = studentCourseSectionService;
    }

    @PostMapping("/getStudentCourseSectionList")
    public DataResponse getStudentCourseSectionList(@Valid @RequestBody DataRequest dataRequest) {
        return studentCourseSectionService.getStudentCourseSectionList(dataRequest);
    }
}

package cn.edu.sdu.java.server.controllers;
import cn.edu.sdu.java.server.models.Notification;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.NotificationService;
import cn.edu.sdu.java.server.services.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @PostMapping("/getNotificationList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getNotificationList(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.getNotificationList(dataRequest);
    }
    @PostMapping("/notificationDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse notificationDelete(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.notificationDelete(dataRequest);
    }
    @PostMapping("/getNotificationInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getNotificationInfo(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.getNotificationInfo(dataRequest);
    }
    @PostMapping("/notificationEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse notificationEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.notificationEditSave(dataRequest);
    }
    @PostMapping("/getNotificationPageData")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getNotificationPageData(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.getNotificationPageData(dataRequest);
    }
    @PostMapping("/getNotificationListExcl")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StreamingResponseBody> getNotificationListExcl(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.getNotificationListExcl(dataRequest);
    }

}



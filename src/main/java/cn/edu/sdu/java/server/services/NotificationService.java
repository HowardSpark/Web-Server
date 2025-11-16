package cn.edu.sdu.java.server.services;
import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SystemService systemService;
    public NotificationService(NotificationRepository notificationRepository,SystemService systemService){
        this.notificationRepository=notificationRepository;
        this.systemService=systemService;
    }
    public Map<String,Object> getMapFromNotification(Notification s) {
        Map<String,Object> m = new HashMap<>();
        if(s==null)
        return m;
        m.put("noticeId",s.getNoticeId());
        m.put("content",s.getContent());
        m.put("releaseTime",s.getReleaseTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));//改了
        m.put("num",s.getNum());
        m.put("title",s.getTitle());
        return m;
    }
    public List<Map<String,Object>> getNotificationMapList(String num) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Notification> sList = notificationRepository.findNotificationListByNumTitle(num);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (Notification notification: sList) {
            dataList.add(getMapFromNotification(notification));
        }
        return dataList;
    }

    public DataResponse getNotificationList(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        List<Map<String,Object>> dataList = getNotificationMapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public DataResponse notificationDelete(DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");  // 获取通知ID
        if (noticeId != null && noticeId > 0) {
            Optional<Notification> op = notificationRepository.findByNoticeId(noticeId);  // 查询通知实体
            op.ifPresent(notificationRepository::delete);  // 若存在则删除
        }
        return CommonMethod.getReturnMessageOK();  // 通知前端操作正常
    }
    public DataResponse getNotificationInfo(DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");
        Notification notice = null;
        Optional<Notification> op;
        if (noticeId != null) {
            op = notificationRepository.findByNoticeId(noticeId); // 根据通知ID查询详情
            if (op.isPresent()) {
                notice = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromNotification(notice)); // 返回
         }
    public DataResponse notificationEditSave(DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");
        Map<String, Object> form = dataRequest.getMap("form"); // 获取表单数据
        String num = CommonMethod.getString(form, "num"); // 通知编号
        Notification notice = null;
        boolean isNew = false;

        // 检查通知编号是否已存在（新增时或修改了编号）
        Optional<Notification> numOp = notificationRepository.findByNum(num);
        if (numOp.isPresent()) {
            // 若存在相同编号，且不是当前编辑的通知，则返回错误
            if (noticeId == null || !numOp.get().getNoticeId().equals(noticeId)) {
                return CommonMethod.getReturnMessageError("通知编号已存在，不能添加或修改！");
            }
        }

        // 查询现有通知（编辑场景）
        if (noticeId != null) {
            Optional<Notification> op = notificationRepository.findByNoticeId(noticeId);
            if (op.isPresent()) {
                notice = op.get();
            }
        }

        // 新增场景：初始化通知对象
        if (notice == null) {
            notice = new Notification();
            isNew = true;
        }

        // 设置通知基本属性（编号、标题、内容）
        notice.setNum(num);
        notice.setTitle(CommonMethod.getString(form, "title"));
        notice.setContent(CommonMethod.getString(form, "content"));

        System.out.println(1);
        // 解析发布时间（前端传递字符串，转换为LocalDateTime）
        String releaseTimeStr = CommonMethod.getString(form, "releaseTime");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        notice.setReleaseTime(LocalDateTime.parse(releaseTimeStr, formatter));
        System.out.println(notice.getReleaseTime());
        System.out.println(1);
        // 保存通知（新增/更新）
        Notification savedNotice = notificationRepository.saveAndFlush(notice);

        // 记录操作日志（假设存在系统日志服务）
        systemService.modifyLog(savedNotice, isNew);

        return CommonMethod.getReturnData(savedNotice.getNoticeId()); // 返回通知ID
    }
    public DataResponse getNotificationPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName"); // 获取搜索关键词（编号/标题）
        Integer currentPage = dataRequest.getCurrentPage(); // 获取当前页码
        int pageSize = 10; // 每页显示条数（与前端分页配置保持一致）
        int totalCount = 0;
        List<Map<String, Object>> dataList = new ArrayList<>();

        // 构建分页参数（Spring Data JPA的Pageable）
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        // 调用Repository分页查询方法
        Page<Notification> page = notificationRepository.findNotificationPageByNumTitle(numName, pageable);

        if (page != null) {
            totalCount = (int) page.getTotalElements(); // 总记录数
            List<Notification> notificationList = page.getContent(); // 当前页数据列表

            // 转换为前端需要的Map结构
            if (!notificationList.isEmpty()) {
                for (Notification notification : notificationList) {
                    Map<String, Object> map = getMapFromNotification(notification);
                    dataList.add(map);
                }
            }
        }

        // 封装返回数据（包含总条数、每页大小、当前页数据）
        Map<String, Object> result = new HashMap<>();
        result.put("dataTotal", totalCount);
        result.put("pageSize", pageSize);
        result.put("dataList", dataList);

        return CommonMethod.getReturnData(result);
    }
    public ResponseEntity<StreamingResponseBody> getNotificationListExcl(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        // 获取需要导出的通知数据列表
        List<Map<String, Object>> list = getNotificationMapList(num);

        // 定义Excel列宽和标题
        Integer[] widths = {8, 20, 30, 15, 50}; // 序号、编号、标题、发布时间、内容
        String[] titles = {"序号", "通知编号", "标题", "发布时间", "内容"};
        String outPutSheetName = "notification.xlsx";

        // 创建Excel工作簿和工作表
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(outPutSheetName);

        // 设置列宽
        for (int j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }

        // 创建单元格样式
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);

        // 创建标题行
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell[] titleCells = new XSSFCell[widths.length];
        for (int j = 0; j < widths.length; j++) {
            titleCells[j] = titleRow.createCell(j);
            titleCells[j].setCellStyle(style);
            titleCells[j].setCellValue(titles[j]);
        }

        // 填充数据行
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                XSSFRow dataRow = sheet.createRow(i + 1);
                XSSFCell[] dataCells = new XSSFCell[widths.length];
                for (int j = 0; j < widths.length; j++) {
                    dataCells[j] = dataRow.createCell(j);
                    dataCells[j].setCellStyle(style);
                }

                Map<String, Object> notificationMap = list.get(i);
                dataCells[0].setCellValue((i + 1) + ""); // 序号
                dataCells[1].setCellValue(CommonMethod.getString(notificationMap, "num")); // 通知编号
                dataCells[2].setCellValue(CommonMethod.getString(notificationMap, "title")); // 标题
                dataCells[3].setCellValue(CommonMethod.getString(notificationMap, "releaseTime")); // 发布时间
                dataCells[4].setCellValue(CommonMethod.getString(notificationMap, "content")); // 内容
            }
        }

        // 构建响应流
        try {
            StreamingResponseBody stream = wb::write;
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



}

package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.*;

@Service
public class CourseSectionService {
    private final CourseSectionRepository courseSectionRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseSectionRepository studentCourseSectionRepository;

    public CourseSectionService(CourseSectionRepository courseSectionRepository, CourseRepository courseRepository, TeacherRepository teacherRepository, StudentRepository studentRepository, StudentCourseSectionRepository studentCourseSectionRepository) {
        this.courseSectionRepository = courseSectionRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.studentCourseSectionRepository = studentCourseSectionRepository;
    }

    /// 获取当前所有课程
    public DataResponse getCourseSectionList(DataRequest dataRequest) {
        Integer studentId = CommonMethod.getPersonId();
        String userType = CommonMethod.getRoleName();
        List<CourseSection> csList;
        if(userType.equals("ROLE_ADMIN")) {
            csList = courseSectionRepository.findAll();
        }else {
            csList = courseSectionRepository.findAvailableCourseSectionList();
        }
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        Course c;
        for (CourseSection cs : csList) {
            m = new HashMap<>();
            Integer courseSectionId = cs.getCourseSectionId();
            m.put("courseSectionId", courseSectionId);
            m.put("num",cs.getNum());
            c = cs.getCourse();
            if(c != null) {
                m.put("courseName", c.getName());
                m.put("courseId", c.getCourseId());
                m.put("credit", c.getCredit());
                m.put("coursePath", c.getCoursePath());
            }
            m.put("place",cs.getPlace());
            m.put("optional",cs.getOptional());
            m.put("time", cs.getTime());

            String teacherName = "无";
            String teacherNum = "无";
            if (cs.getTeacher() != null) { // 先判断教师是否存在
                Person teacherPerson = cs.getTeacher().getPerson();
                if (teacherPerson != null) { // 再判断教师的Person是否存在
                    teacherName = teacherPerson.getName();
                    teacherNum = teacherPerson.getNum();
                }
            }
            m.put("teacherName",teacherName);
            m.put("teacherNum",teacherNum);

            boolean isSelected = false;
            // 只有「学生已登录」且「课程段ID有效」时，才校验已选状态
            if (studentId != null && courseSectionId != null) {
                // 调用Repository方法，校验中间表是否存在关联
                isSelected = studentCourseSectionRepository.existsByStudentPersonIdAndCourseSectionCourseSectionId(studentId, courseSectionId);
            }
            m.put("status", isSelected);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse courseSectionSave(DataRequest dataRequest) {
        Integer courseSectionId = dataRequest.getInteger("courseSectionId");
        String num = dataRequest.getString("num");
        String place = dataRequest.getString("place");
        Boolean optional = dataRequest.getBoolean("optional");
        Integer time = dataRequest.getInteger("time");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer teacherId = dataRequest.getInteger("teacherId");
        Optional<CourseSection> opCs;
        CourseSection cs = null;

        //先查找是否已有courseSection
        if(courseSectionId != null) {
            opCs = courseSectionRepository.findById(courseSectionId);
            if(opCs.isPresent())
                cs = opCs.get();
        }
        //若之前没有，则新建courseSection
        if(cs == null) {
            cs = new CourseSection();
        }
        //处理course相关信息
        Optional<Course> opC;
        Course c =null;
        if(courseId != null) {
            opC = courseRepository.findById(courseId);
            if(opC.isPresent())
                c = opC.get();
        }

        // 3. 新增关联教师（关键修复：根据 teacherId 查询教师并设置）
        Optional<Teacher> opTeacher;
        Teacher t = null;
        if(teacherId != null) {
            opTeacher = teacherRepository.findById(teacherId);
            if(opTeacher.isPresent()) {
                t = opTeacher.get();
            }
        }

        cs.setNum(num);
        cs.setPlace(place);
        cs.setOptional(optional);
        cs.setTime(time);
        cs.setCourse(c);
        cs.setTeacher(t);
        courseSectionRepository.save(cs);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse courseSectionDelete(DataRequest dataRequest) {
        Integer courseSectionId = dataRequest.getInteger("courseSectionId");
        Optional<CourseSection> op;
        CourseSection cs = null;
        if(courseSectionId != null) {
            op = courseSectionRepository.findById(courseSectionId);
            if(op.isPresent()) {
                cs = op.get();
                courseSectionRepository.delete(cs);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse courseSectionPublish(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        System.out.println(num);
        CourseSection cs = courseSectionRepository.findByNum(num).orElse(null);
        if(cs ==null){
            return new DataResponse(1,null,"发生错误1");
        }
        cs.setOptional(!cs.getOptional());
        System.out.println(cs.getOptional());
        courseSectionRepository.save(cs);
        return new DataResponse(0,null,"修改成功");
    }

    public DataResponse courseSectionSelect(DataRequest dataRequest) {
        Integer courseSectionId = dataRequest.getInteger("courseSectionId");
        System.out.println(courseSectionId);
//        System.out.println(studentId);
        Optional<CourseSection> opcs;
        Optional<Student> ops;
        StudentCourseSection scs = null;
        CourseSection cs = null;
        Student st = null;

        if(courseSectionId != null) {
            opcs = courseSectionRepository.findById(courseSectionId);
            if(opcs.isPresent())
                cs = opcs.get();
        }

        if(cs == null){
            return CommonMethod.getReturnMessageError("Invalid courseSectionId");
        }

        scs = new StudentCourseSection();
        scs.setCourseSection(cs);
        scs.setStudent(studentRepository.findByPersonNum(CommonMethod.getUsername()).get());

        studentCourseSectionRepository.save(scs);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse courseSectionUnselect(DataRequest dataRequest) {
        Integer courseSectionId = dataRequest.getInteger("courseSectionId");
        Integer studentId = CommonMethod.getPersonId();
        Optional<StudentCourseSection> op;
        StudentCourseSection scs = null;
        if(courseSectionId != null && studentId != null) {
            op = studentCourseSectionRepository.findByPersonCourseSection(studentId, courseSectionId);
            if(op.isPresent()) {
                scs = op.get();
                studentCourseSectionRepository.delete(scs);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    /// 获取当前所有可选课程
    public DataResponse getAvailableCourseSectionList(DataRequest dataRequest){
        List<CourseSection> list = courseSectionRepository.findAvailableCourseSectionList();
        return getResponseFromList(list);
    }
    /// 获取当前所有不可选课程
    public DataResponse getUnavailableCourseSectionList(DataRequest dataRequest){
        List<CourseSection> list = courseSectionRepository.findUnavailableCourseSectionList();
        return getResponseFromList(list);
    }

    public OptionItemList getTeacherItemOptionList(DataRequest dataRequest) {
        List<Teacher> list = teacherRepository.findAll();
        List<OptionItem> ret = new ArrayList<>();
        for( Teacher i : list ){
            ret.add(new OptionItem(i.getPersonId(), i.getPerson().getNum(),i.getPerson().getNum()+"-"+i.getPerson().getName()));
        }
        return new OptionItemList(0,ret);
    }

    public OptionItemList getTimeItemOptionList(DataRequest dataRequest) {
        // 1. 星期编码→名称映射（0=星期日，1=星期一，…，6=星期六）
        String[] weekNames = {
                "星期日", "星期一", "星期二", "星期三",
                "星期四", "星期五", "星期六"
        };

        // 2. 初始化选项列表
        List<OptionItem> optionList = new ArrayList<>();

        // 3. 循环生成所有「星期-节次」组合（7天×5节=35个选项）
        for (int weekCode = 0; weekCode < 7; weekCode++) { // 星期编码：0-6
            String weekName = weekNames[weekCode];
            for (int classNo = 1; classNo <= 5; classNo++) { // 节次：1-5
                // 生成编码字符串（value）：三位数，如 "101"、"003"
                String value = String.format("%d%02d", weekCode, classNo);
                // 生成 id：将编码字符串转为数字（如 "101" → 101，唯一标识）
                Integer id = Integer.parseInt(value);
                // 生成前端显示文本（title）：如 "星期一第1节"
                String title = String.format("%s第%d节", weekName, classNo);
                // 构造 OptionItem 并添加到列表（对应你的三参数构造器）
                optionList.add(new OptionItem(id, value, title));
            }
        }

        return new OptionItemList(0,optionList);
    }

    private DataResponse getResponseFromList(List<CourseSection> list){
        List< Map<String, Object> > ret = new ArrayList<>();
        for(CourseSection cs : list){
            Map<String, Object> map = new HashMap<>();
            map.put("courseSectionId",cs.getCourseSectionId());
            map.put("courseId",cs.getCourse().getCourseId());
            map.put("courseSectionNum", cs.getNum());
            map.put("courseNum", cs.getCourse().getNum());
            map.put("courseName", cs.getCourse().getName());
            map.put("credit", cs.getCourse().getCredit());
            map.put("optional", cs.getOptional()?"可选":"不可选");
            map.put("time", cs.getTime());
            map.put("place", cs.getPlace());
            map.put("teacherName", cs.getTeacher().getPerson().getName());
            map.put("teacherNum", cs.getTeacher().getPerson().getNum());
            ret.add(map);
        }
        return new DataResponse(0,ret,"");
    }
}

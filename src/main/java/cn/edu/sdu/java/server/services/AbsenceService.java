package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Absence;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.AbsenceRepository;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AbsenceService {
    StudentRepository studentRepository;
    TeacherRepository teacherRepository;
    PersonRepository personRepository;
    AbsenceRepository absenceRepository;
    @Autowired
    public AbsenceService(TeacherRepository teacherRepository, PersonRepository personRepository, AbsenceRepository absenceRepository, StudentRepository studentRepository) {
        this.teacherRepository = teacherRepository;
        this.personRepository = personRepository;
        this.absenceRepository = absenceRepository;
        this.studentRepository = studentRepository;
    }
    public DataResponse getAbsenceList() {
        Integer personId = CommonMethod.getPersonId();//先获取到自己的personid，后面再做角色判断，学生一个查询方法，老师一个查询方法
        if (Objects.equals(CommonMethod.getRoleName(), "ROLE_STUDENT")) {
            List<Absence> absences;
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> m;
            Absence ab;
            try {
                Student student = studentRepository.findByPersonId(personId);
                absences = absenceRepository.findByStudent(student);
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("获取请假列表失败");
            }
            for (Absence absence : absences) {
                if(absence.getIsDeletedStudent()) {
                continue;
                }
                m = new HashMap<>();
                m.put("absenceId", absence.getAbsenceId());
                m.put("absenceType", absence.getAbsenceType());
                m.put("beginTime", absence.getBeginTime());
                m.put("endTime", absence.getEndTime());
                Person person = personRepository.findByPersonId(absence.getTeacher().getPersonId());
                m.put("teacher", person.getName());
                m.put("result", absence.getResult());
                m.put("absenceReason", absence.getAbsenceReason());
                dataList.add(m);
            }
            return CommonMethod.getReturnData(dataList);
        } else if (Objects.equals(CommonMethod.getRoleName(), "ROLE_TEACHER")) {
            List<Absence> absences;
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> m;
            Absence ab;
            try {
                Teacher teacher = teacherRepository.findByPersonId(personId);
                absences = absenceRepository.findByTeacher(teacher);
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("获取请假列表失败");
            }
            for (Absence absence : absences) {
                if(absence.getIsDeletedTeacher()) {
                    continue;
                }
                m = new HashMap<>();
                m.put("absenceId", absence.getAbsenceId());
                m.put("absenceType", absence.getAbsenceType());
                m.put("beginTime", absence.getBeginTime());
                m.put("endTime", absence.getEndTime());
                Person person = personRepository.findByPersonId(absence.getStudent().getPersonId());
                m.put("student", person.getName());
                m.put("result", absence.getResult());
                m.put("absenceReason", absence.getAbsenceReason());
                dataList.add(m);
            }
            return CommonMethod.getReturnData(dataList);
        }
        else{
            List<Absence> absences;
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> m;
            Absence ab;
            try {
                absences = absenceRepository.findAll();
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("获取请假列表失败");
            }
            for (Absence absence : absences) {
                if(absence.getIsDeletedAdmin()) {
                    continue;
                }
                m = new HashMap<>();
                m.put("absenceId", absence.getAbsenceId());
                m.put("absenceType", absence.getAbsenceType());
                m.put("beginTime", absence.getBeginTime());
                m.put("endTime", absence.getEndTime());
                Person person1 = personRepository.findByPersonId(absence.getTeacher().getPersonId());
                Person person2 = personRepository.findByPersonId(absence.getStudent().getPersonId());
                m.put("teacher", person1.getName());
                m.put("student", person2.getName());
                m.put("result", absence.getResult());
                m.put("absenceReason", absence.getAbsenceReason());
                dataList.add(m);
            }
            return CommonMethod.getReturnData(dataList);

        }
    }

    public DataResponse absenceSave(@Valid DataRequest dataRequest) {
        Integer personId = CommonMethod.getPersonId();
        Integer teacherId = dataRequest.getInteger("teacherId");
        String beginTime = dataRequest.getString("beginTime");
        String endTime = dataRequest.getString("endTime");
        String absenceType = dataRequest.getString("absenceType");
        String absenceReason = dataRequest.getString("absenceReason");
        Absence absence = new Absence();
        Student student = studentRepository.findByPersonId(personId);//通过用户的ID找到身为学生的ID
        Teacher teacher = teacherRepository.findByPersonId(teacherId);//找到对应的老师
        if (teacher == null) {
            return CommonMethod.getReturnMessageError("请检查您的教师信息是否正确");
        }
        absence.setStudent(student);
        absence.setTeacher(teacher);
        absence.setAbsenceReason(absenceReason);
        absence.setAbsenceType(absenceType);
        absence.setBeginTime(beginTime);
        absence.setEndTime(endTime);
        absence.setIsDeletedTeacher(false);
        absence.setIsDeletedStudent(false);
        absence.setIsDeletedAdmin(false);
        absence.setResult("待审批");
        absenceRepository.save(absence);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse absenceDelete(@Valid DataRequest dataRequest) {
        if (Objects.equals(CommonMethod.getRoleName(), "ROLE_STUDENT")) {
            Integer absenceId = dataRequest.getInteger("absenceId");
            try {
                Optional<Absence> absenceOpt = absenceRepository.findById(absenceId);
                if (absenceOpt.isPresent()) {
                    Absence absence = absenceOpt.get();
                    absence.setIsDeletedStudent(true);
                    absenceRepository.save(absence);
                }
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("删除失败");
            }
            return CommonMethod.getReturnMessageOK();
        } else if (Objects.equals(CommonMethod.getRoleName(), "ROLE_TEACHER")) {
            Integer absenceId = dataRequest.getInteger("absenceId");
            try {
                Optional<Absence> absenceOpt = absenceRepository.findById(absenceId);
                if (absenceOpt.isPresent()) {
                    Absence absence = absenceOpt.get();
                    absence.setIsDeletedTeacher(true);
                    absenceRepository.save(absence);
                }
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("删除失败");
            }
            return CommonMethod.getReturnMessageOK();
        } else {
            Integer absenceId = dataRequest.getInteger("absenceId");
            try {
                Optional<Absence> absenceOpt = absenceRepository.findById(absenceId);
                if (absenceOpt.isPresent()) {
                    Absence absence = absenceOpt.get();
                    absence.setIsDeletedAdmin(true);
                    absenceRepository.save(absence);
                }
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("删除失败");
            }
            return CommonMethod.getReturnMessageOK();
        }

    }

    public OptionItemList getTeacherItemOptionList(DataRequest dataRequest) {
        List<Teacher> list = teacherRepository.findAll();
        List<OptionItem> ret = new ArrayList<>();
        for( Teacher i : list ) {
            ret.add(new OptionItem(i.getPersonId(), i.getPerson().getNum(), i.getPerson().getNum() + "-" + i.getPerson().getName()));
        }
        return new OptionItemList(0,ret);
    }
}

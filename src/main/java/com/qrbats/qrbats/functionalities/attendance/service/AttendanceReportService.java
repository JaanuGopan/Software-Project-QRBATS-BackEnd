package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;

import java.util.List;

public interface AttendanceReportService {
    List<Student> getAllStudentsByDepartmentIdAndSemester(Integer deptID,Integer sem);
    List<AttendanceLecture> getAllAttendanceByLectureId(Integer lectureId);
}

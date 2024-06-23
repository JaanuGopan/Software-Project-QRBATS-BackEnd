package com.qrbats.qrbats.functionalities.export.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;

public interface ExportService {
    ByteArrayInputStream exportLectureAttendance(Integer lectureId) throws IOException;
    ByteArrayInputStream exportLectureAttendanceByLectureIdAndDate(Integer lectureId, Date date) throws IOException;
    ByteArrayInputStream exportEventAttendance(Integer lectureId) throws IOException;
}

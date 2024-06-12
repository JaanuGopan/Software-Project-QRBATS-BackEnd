package com.qrbats.qrbats.functionalities.export.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ExportService {
    ByteArrayInputStream exportLectureAttendance(Integer lectureId) throws IOException;
    ByteArrayInputStream exportEventAttendance(Integer lectureId) throws IOException;
}

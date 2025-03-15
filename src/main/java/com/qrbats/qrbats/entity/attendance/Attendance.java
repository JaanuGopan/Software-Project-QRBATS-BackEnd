package com.qrbats.qrbats.entity.attendance;

import com.qrbats.qrbats.service.AttendanceDataJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer attendeeId;

    @Convert(converter = AttendanceDataJsonConverter.class)
    private AttendanceData attendanceData;
}

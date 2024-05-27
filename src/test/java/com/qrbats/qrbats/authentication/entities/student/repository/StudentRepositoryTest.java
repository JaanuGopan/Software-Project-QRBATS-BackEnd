package com.qrbats.qrbats.authentication.entities.student.repository;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.StudentRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class StudentRepositoryTest {
    @Autowired
    private StudentRepository underTestStudentRepo;

    private Student addSampleStudent(){
        Student student = new Student();
        student.setStudentName("testStudentName");
        student.setStudentEmail("testStudentEmail@gmail.com");
        student.setStudentRole(StudentRole.UORSTUDENT);
        student.setUserName("test");
        student.setPassword(new BCryptPasswordEncoder().encode("test"));
        student.setCurrentSemester(6);
        student.setDepartmentId(2);
        student.setIndexNumber("EG/2020/1234");

        return underTestStudentRepo.save(student);
    }

    private void deleteSampleStudent(Student student){
        underTestStudentRepo.delete(student);
    }

    @Test
    void findByUserName() {
        // given
        String userName = "test";
        Student sampleStudent = addSampleStudent();

        // when
        Optional<Student> expectedStudent = underTestStudentRepo.findByUserName(userName);

        // then
        assertThat(expectedStudent.isPresent()).isTrue();
        assertThat(expectedStudent.get().getUsername()).isEqualTo(userName);

        deleteSampleStudent(sampleStudent);

    }

    @Test
    void findByStudentEmail() {
        // given
        String studentEmail = "testStudentEmail@gmail.com";
        Student sampleStudent = addSampleStudent();

        // when
        Student expectedStudent = underTestStudentRepo.findByStudentEmail(studentEmail);

        // then
        assertThat(expectedStudent.getStudentEmail()).isEqualTo(studentEmail);

        deleteSampleStudent(sampleStudent);
    }

    @Test
    void findByIndexNumber() {
        // given
        String indexNo = "EG/2020/1234";
        Student sampleStudent = addSampleStudent();

        // when
        Optional<Student> expectedStudent = underTestStudentRepo.findByIndexNumber(indexNo);

        // then
        assertThat(expectedStudent.get().getIndexNumber()).isEqualTo(indexNo);

        deleteSampleStudent(sampleStudent);
    }
}
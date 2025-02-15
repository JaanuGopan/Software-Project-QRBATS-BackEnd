package com.qrbats.qrbats.authentication.controller;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserService userService;

    @GetMapping("/get-all-staffs")
    public ResponseEntity<List<User>> getAllStaffs(){
        return ResponseEntity.ok(userService.getAllStaffs());
    }

    @GetMapping("/get-all-students")
    public ResponseEntity<List<User>> getAllStudents(){
        return ResponseEntity.ok(userService.getAllStudent());
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUserById(@RequestParam Integer userId){
        try {
            return ResponseEntity.ok(userService.deleteByUserId(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

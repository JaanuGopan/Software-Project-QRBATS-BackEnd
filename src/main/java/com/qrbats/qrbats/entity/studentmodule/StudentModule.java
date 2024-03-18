/*
package com.authendicationjwt.security.entity.studentmodule;

import com.authendicationjwt.security.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "StudentModule")
public class StudentModule {
    @Id
    @ManyToOne
    @JoinColumn(name = "User_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "Module_id")
    private Module module;
}
*/

package com.example.demo.users.entity;

import com.example.demo.auth.entity.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data // provides getters, setters, equals, hashcode, toString
@Entity
@Table(name = "_user") // in postgresql and jpa/hibernate, user is a reserved word
//@SQLRestriction("deleted = false") // this is a custom annotation // like @Where
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    private Long id;

    private String firstname;
    private String lastname;


    @Column(nullable = false, unique = true)
    private String email;

    @Column(columnDefinition = "boolean default true")
    private boolean status = true;

    private String password;

    @Column(updatable = false) // prevents update
    private LocalDateTime createdAt; // timestamp
    private LocalDateTime updatedAt; // timestamp


    // // @ManyToOne - crea AQUI la FK - one role can have many users
    @ManyToOne(fetch = FetchType.EAGER) // eager fetch - populate the role object when querying for user
    @JoinColumn(name = "role_id")
    private Role role;

// soft delete
//    @Column(columnDefinition = "boolean default false")
//    private boolean deleted;


    // // @PrePersist - before insert - when AuditingEntityListener is not used
    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

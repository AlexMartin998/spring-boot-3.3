package com.example.demo.auth.repository;

import com.example.demo.auth.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface RoleRepository extends CrudRepository<Role, Long> { // model, pk type

    Optional<Role> findByName(String name);

}

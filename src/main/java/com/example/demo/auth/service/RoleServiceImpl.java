package com.example.demo.auth.service;

import com.example.demo.auth.entity.Role;
import com.example.demo.auth.repository.RoleRepository;
import com.example.demo.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor // generates a constructor with all class fields as arguments - DI
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;


    @Override
    public Role findOneByName(String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException("Role", "name", name)
        );
    }

}

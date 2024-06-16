package com.example.demo.auth.service;

import com.example.demo.auth.entity.Role;


public interface RoleService {

    Role findOneByName(String name);

}

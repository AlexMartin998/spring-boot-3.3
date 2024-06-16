package com.example.demo.users.service;

import com.example.demo.users.dto.PaginatedUsersResponseDto;
import com.example.demo.users.dto.UserRequestDto;
import com.example.demo.users.dto.UserResponseDto;
import com.example.demo.users.entity.Usuario;
import org.springframework.data.domain.Pageable;


public interface UserService {

    Usuario create(Usuario user);

    Usuario findOneByEmail(String email);

    PaginatedUsersResponseDto findAll(Pageable pageable);

    Usuario findOne(Long id);

    UserResponseDto update(Long id, UserRequestDto userRequestDto);

}

package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthResponseDto;
import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.RegisterRequestDto;

import java.util.List;


public interface AuthService {

    AuthResponseDto register(RegisterRequestDto registerRequestDto);

    AuthResponseDto login(LoginRequestDto loginRequestDto);

    AuthResponseDto renewJwt(String email);

    List<AuthResponseDto.RoleDto> findAllRoles();

}

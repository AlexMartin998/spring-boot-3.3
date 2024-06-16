package com.example.demo.users.dto;

import com.example.demo.auth.dto.AuthResponseDto;
import lombok.Data;


@Data
public class UserResponseDto {

    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private boolean deleted;
    private AuthResponseDto.RoleDto role;

}

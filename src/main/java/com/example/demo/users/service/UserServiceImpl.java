package com.example.demo.users.service;

import com.example.demo.auth.repository.RoleRepository;
import com.example.demo.users.dto.PaginatedUsersResponseDto;
import com.example.demo.users.dto.UserRequestDto;
import com.example.demo.users.dto.UserResponseDto;
import com.example.demo.users.entity.Usuario;
import com.example.demo.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor // DI - final fields
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public Usuario create(Usuario user) {
        return null;
    }

    @Override
    public Usuario findOneByEmail(String email) {
        return null;
    }

    @Override
    public PaginatedUsersResponseDto findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Usuario findOne(Long id) {
        return null;
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        return null;
    }

}

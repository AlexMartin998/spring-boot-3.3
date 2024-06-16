package com.example.demo.users.service;

import com.example.demo.auth.constants.RoleConstants;
import com.example.demo.auth.entity.Role;
import com.example.demo.auth.repository.RoleRepository;
import com.example.demo.shared.exceptions.BadRequestException;
import com.example.demo.shared.exceptions.ResourceNotFoundException;
import com.example.demo.shared.exceptions.UserNotFoundException;
import com.example.demo.users.dto.PaginatedUsersResponseDto;
import com.example.demo.users.dto.UserRequestDto;
import com.example.demo.users.dto.UserResponseDto;
import com.example.demo.users.entity.Usuario;
import com.example.demo.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        Role role = roleRepository.findByName(RoleConstants.USER).orElseThrow(
                () -> new ResourceNotFoundException("Role", "name", RoleConstants.USER)
        );
        user.setRole(role); // Collections.singleton()  --> return  Set<T>

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findOneByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                // CustomUserDetailsService needs this exception
                () -> new UsernameNotFoundException("User not found with email: ".concat(email))
        );
    }

    @Override
    public PaginatedUsersResponseDto findAll(Pageable pageable) {
        Page<Usuario> userPage = userRepository.findAll(pageable);
        List<UserResponseDto> userDtoList = userPage.getContent().stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .toList();

        return PaginatedUsersResponseDto.builder()
                .users(userDtoList)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .isLastOne(userPage.isLast())
                .build();
    }

    @Override
    public Usuario findOne(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: ".concat(id.toString()))
        );
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        Usuario user = findOneById(id);
        Role role = roleRepository.findById(userRequestDto.getRoleId()).orElseThrow(
                () -> new ResourceNotFoundException("Role", "ID", userRequestDto.getRoleId())
        );

        Usuario userToSave = modelMapper.map(userRequestDto, Usuario.class);
        userToSave.setId(id);
        userToSave.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        userToSave.setRole(role);

        return modelMapper.map(userRepository.save(userToSave), UserResponseDto.class);
    }


    private Usuario findOneById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not fount with ID: ".concat(id.toString()))
        );
    }

}

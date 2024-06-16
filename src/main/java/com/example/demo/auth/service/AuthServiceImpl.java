package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthResponseDto;
import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.RegisterRequestDto;
import com.example.demo.auth.entity.Role;
import com.example.demo.auth.jwt.UserDetailsImpl;
import com.example.demo.auth.repository.RoleRepository;
import com.example.demo.users.entity.Usuario;
import com.example.demo.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service // set as @Bean to inject it
@RequiredArgsConstructor // DI final fields
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public AuthResponseDto register(RegisterRequestDto registerRequestDto) {
        Usuario newUser = createUserFromDto(registerRequestDto);
        Usuario createdUser = userService.create(newUser);

        return generateAuthResponse(createdUser.getEmail());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate((
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        ));

        return generateAuthResponse(loginRequestDto.getEmail());
    }

    @Override
    public AuthResponseDto renewJwt(String email) {
        return generateAuthResponse(email);
    }

    @Override
    public List<AuthResponseDto.RoleDto> findAllRoles() {
        List<Role> roles = (List<Role>) roleRepository.findAll(); // iterable to list

        return roles.stream().map(role -> modelMapper.map(
                role, AuthResponseDto.RoleDto.class
        )).toList();
    }


    // // private methods -----------
    private Usuario createUserFromDto(RegisterRequestDto registerDto) {
        Usuario newUser = modelMapper.map(registerDto, Usuario.class);
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        return newUser;
    }

    private AuthResponseDto generateAuthResponse(String userEmail) {
        // get userDetails and validate if it exists
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        String jwtToken = jwtService.generateJwt(userDetails);
        AuthResponseDto.UserDto userDto = modelMapper.map(
                ((UserDetailsImpl) userDetails).getUser(),
                AuthResponseDto.UserDto.class
        );

        return AuthResponseDto.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }

}

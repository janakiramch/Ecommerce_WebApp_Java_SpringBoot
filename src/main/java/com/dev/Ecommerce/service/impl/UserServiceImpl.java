package com.dev.Ecommerce.service.impl;

import com.dev.Ecommerce.dto.LoginRequest;
import com.dev.Ecommerce.dto.Response;
import com.dev.Ecommerce.dto.UserDto;
import com.dev.Ecommerce.entity.User;
import com.dev.Ecommerce.enums.UserRole;
import com.dev.Ecommerce.exception.InvalidCredentialsException;
import com.dev.Ecommerce.mapper.EntityDtoMapper;
import com.dev.Ecommerce.repository.UserRepo;
import com.dev.Ecommerce.security.JwtUtils;
import com.dev.Ecommerce.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EntityDtoMapper entityDtoMapper;



    @Override
    public Response registerUser(UserDto registrationRequest) {
        UserRole role = UserRole.USER;

        if(registrationRequest.getRole()!= null && registrationRequest.getRole().equalsIgnoreCase("admin")){
            role = UserRole.ADMIN;
        }

        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(role)
                .build();

        User savedUser = userRepo.save(user);

        UserDto userDto = entityDtoMapper.mapUserToDtoBasic(savedUser);

        return Response.builder()
                .status(200)
                .message("User successfully added")
                .user(userDto)
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
       User user = userRepo.findByEmail(loginRequest.getEmail())
                           .orElseThrow(()->new NotAcceptableStatusException("Email Not Found"));
       if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
           throw new InvalidCredentialsException("Password doesnot match. Try again");
       }

       String token = jwtUtils.generateToken(user);

       return Response.builder()
               .status(200)
               .message("User Login Successfull")
               .token(token)
               .expirationTime("6 Month")
               .role(user.getRole().name())
               .build();

    }

    @Override
    public Response getAllUsers() {

        List<User> users = userRepo.findAll();
        List<UserDto> userDtos = users.stream()
                .map(entityDtoMapper::mapUserToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .message("Successfull")
                .userList(userDtos)
                .build();
    }

    @Override
    public User getLoginUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("User Email is: " + email);

        return userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user Not Found"));

    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        User user = getLoginUser();
        UserDto userDto = entityDtoMapper.mapUserToDtoPlusAddressAndOrderHistory(user);

        return Response.builder()
                .status(200)
                .user(userDto)
                .build();
    }
}

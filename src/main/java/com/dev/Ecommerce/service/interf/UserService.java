package com.dev.Ecommerce.service.interf;

import com.dev.Ecommerce.dto.LoginRequest;
import com.dev.Ecommerce.dto.Response;
import com.dev.Ecommerce.dto.UserDto;
import com.dev.Ecommerce.entity.User;

public interface UserService {
    Response registerUser(UserDto registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();
}

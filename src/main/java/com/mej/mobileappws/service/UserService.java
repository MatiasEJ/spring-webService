package com.mej.mobileappws.service;

import com.mej.mobileappws.model.request.OperationStatusModel;
import com.mej.mobileappws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto getUser(String nameOfUser);
    
    UserDto getUserByUserId(String userID);
    
    UserDto updateUser(String userId, UserDto userDto);
    
    void deleteUser(String userId);
    
    List<UserDto> getUsers(int argPage, int argLimit);
}

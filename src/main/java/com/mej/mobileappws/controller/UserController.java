package com.mej.mobileappws.controller;

import com.mej.mobileappws.model.request.UserDetailRequestModel;
import com.mej.mobileappws.model.response.UserRest;
import com.mej.mobileappws.service.impl.UserServiceImpl;
import com.mej.mobileappws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {
    
    @Autowired
    private UserServiceImpl userService;
    
    
    @GetMapping(
        path = "/{userId}",
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String userId) {
        UserRest returnValue = new UserRest();
        UserDto  userDto     = userService.getUserByUserId(userId);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }
    
    @PostMapping(
        consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailRequestModel userDetails) {
        UserRest returnValue = new UserRest();
        UserDto  userDto     = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);
        return returnValue;
        
        
    }
    
    @PutMapping
    public String updateUser() {
        return "update user was called";
    }
    
    @DeleteMapping
    public String deleteUser() {
        return "user deleted";
    }
    
}

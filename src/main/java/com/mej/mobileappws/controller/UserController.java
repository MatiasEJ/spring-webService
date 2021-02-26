package com.mej.mobileappws.controller;

import com.mej.mobileappws.exceptions.UserServiceException;
import com.mej.mobileappws.model.request.UserDetailRequestModel;
import com.mej.mobileappws.model.response.ErrorMessages;
import com.mej.mobileappws.model.response.UserRest;
import com.mej.mobileappws.service.impl.UserServiceImpl;
import com.mej.mobileappws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @PostMapping(
        consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();
        UserDto  userDto     = new UserDto();
        
        if (userDetails.getFirstName().isEmpty()){
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage()+" "+userDetails.getFirstName());
        }
        
        
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

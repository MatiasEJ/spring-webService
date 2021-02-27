package com.mej.mobileappws.controller;

import com.mej.mobileappws.model.request.OperationStatusModel;
import com.mej.mobileappws.model.request.RequestOperationName;
import com.mej.mobileappws.model.request.RequestOperationStatus;
import com.mej.mobileappws.model.request.UserDetailRequestModel;
import com.mej.mobileappws.model.response.UserRest;
import com.mej.mobileappws.service.impl.UserServiceImpl;
import com.mej.mobileappws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public UserRest createUser(@RequestBody UserDetailRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();
        
        ModelMapper modelMapper = new ModelMapper();
        UserDto  userDto     =    modelMapper.map(userDetails,UserDto.class);
       
        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);
        return returnValue;
        
        
    }
    
    @PutMapping(
        path = "/{userId}",
        consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(
        @RequestBody UserDetailRequestModel userDetails,
        @PathVariable String userId) {
        UserRest returnValue = new UserRest();
        UserDto  userDto     = new UserDto();
        
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto createdUser = userService.updateUser(userId, userDto);
        BeanUtils.copyProperties(createdUser, returnValue);
        return returnValue;
    }
    
    @DeleteMapping(
        path = "/{userId}",
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public OperationStatusModel deleteUser(@PathVariable String userId) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        
        userService.deleteUser(userId);
        
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        
        return returnValue;
    }
    
    @GetMapping(
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(
        @RequestParam(value = "page", defaultValue = "0") int argPage,
        @RequestParam(value = "limit", defaultValue = "25") int argLimit) {
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(argPage,argLimit);
        
        for (UserDto userDto : users){
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto,userModel);
            returnValue.add(userModel);
        }
        return returnValue;
    }
}

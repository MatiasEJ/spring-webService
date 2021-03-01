package com.mej.mobileappws.controller;

import com.mej.mobileappws.model.request.OperationStatusModel;
import com.mej.mobileappws.model.request.RequestOperationName;
import com.mej.mobileappws.model.request.RequestOperationStatus;
import com.mej.mobileappws.model.request.UserDetailRequestModel;
import com.mej.mobileappws.model.response.AddressesRest;
import com.mej.mobileappws.model.response.UserRest;
import com.mej.mobileappws.service.AddressService;
import com.mej.mobileappws.service.impl.UserServiceImpl;
import com.mej.mobileappws.shared.dto.AddressDTO;
import com.mej.mobileappws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserServiceImpl userService;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private AddressService addressesService;
    
    
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
        UserRest    returnValue;
        ModelMapper modelMapper = new ModelMapper();
        UserDto     userDto     = modelMapper.map(userDetails, UserDto.class);
        UserDto     createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);
        
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
        List<UserDto>  users       = userService.getUsers(argPage, argLimit);
        
        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }
        return returnValue;
    }
    
    //    http://localhost:8080/mobile-app-ws/users/asdasds/addresses
    @GetMapping(
        path = "/{userId}/addresses",
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String userId) {
        ModelMapper         modelMapper  = new ModelMapper();
        List<AddressesRest> addressesRestList  = new ArrayList<>();
        List<AddressDTO>    addressesDto = addressesService.getAddresses(userId);
        
        
        
        if (addressesDto != null && !addressesDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            addressesRestList = modelMapper.map(addressesDto, listType);
        
            addressesRestList.stream().forEach(addressesRest -> {
    
                Link addressLink = linkTo(methodOn(UserController.class)
                                              .getUserAddress(userId, addressesRest.getAddressId())).withSelfRel();
                addressesRest.add(addressLink);
                Link userLink      = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
    
                addressesRest.add(userLink);
            });
            
            return CollectionModel.of(addressesRestList);
            
            
        }
        return  CollectionModel.empty();
    }
    
    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,"application/hal+json"})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String addressId, @PathVariable String userId) {
        AddressDTO  addressDTO  = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();
        
        
        Link addressLink = linkTo(methodOn(UserController.class)
                                      .getUserAddress(userId, addressId)).withSelfRel();
        Link userLink      = linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
        
        AddressesRest addressesRestModel = modelMapper.map(addressDTO, AddressesRest.class);
        addressesRestModel.add(addressLink);
        addressesRestModel.add(userLink);
        addressesRestModel.add(addressesLink);
        
        return EntityModel.of(addressesRestModel) ;
    }
    
}

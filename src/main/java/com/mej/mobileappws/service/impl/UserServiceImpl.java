package com.mej.mobileappws.service.impl;

import com.mej.mobileappws.exceptions.UserServiceException;
import com.mej.mobileappws.model.request.OperationStatusModel;
import com.mej.mobileappws.model.response.ErrorMessages;
import com.mej.mobileappws.repository.UserRepository;
import com.mej.mobileappws.entity.UserEntity;
import com.mej.mobileappws.service.UserService;
import com.mej.mobileappws.shared.dto.UserDto;
import com.mej.mobileappws.shared.dto.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    Utils utils;
    
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Override
    public UserDto createUser(UserDto user) {
        
        if(userRepository.findByEmail(user.getEmail())!= null){
            throw new RuntimeException("Record already exist");
        }
        
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);
        String publicUserId = utils.generateUserId(30);
        
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        
        UserEntity storedUserDetails =  userRepository.save(userEntity);
        
        UserDto returnValue =  new UserDto();
        BeanUtils.copyProperties(storedUserDetails,returnValue);
        
        return returnValue;
    }
    
    @Override
    public UserDto getUser(String email) {
    
        UserEntity userEntity =  userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }
    
    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
      
        if(userEntity== null){
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        
        BeanUtils.copyProperties(userEntity,returnValue);
        
        return returnValue;
    }
    
    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
    
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        UserEntity updatedUserDetails =  userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserDetails,returnValue);
        return returnValue;
        
    }
    
    @Override
    public void deleteUser(String userId) {
    
        UserEntity userEntity = userRepository.findByUserId(userId);
    
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        userRepository.delete(userEntity);
    
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity =  userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
        
    }
    
    
    
    
    
}

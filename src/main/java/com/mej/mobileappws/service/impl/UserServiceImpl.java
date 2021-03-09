package com.mej.mobileappws.service.impl;

import com.mej.mobileappws.entity.UserEntity;
import com.mej.mobileappws.exceptions.UserServiceException;
import com.mej.mobileappws.model.response.ErrorMessages;
import com.mej.mobileappws.repository.UserRepository;
import com.mej.mobileappws.service.UserService;
import com.mej.mobileappws.shared.dto.AddressDTO;
import com.mej.mobileappws.shared.dto.UserDto;
import com.mej.mobileappws.shared.dto.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDto createUser(UserDto user) {
        
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Record already exist");
        }

        for (int i = 0; i<user.getAddresses().size(); i++){
            AddressDTO address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, address);
        }
      
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        
        
        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        
        UserEntity storedUserDetails = userRepository.save(userEntity);
        
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);
        
        return returnValue;
    }
    
    @Override
    public UserDto getUser(String email) {
        
        UserEntity userEntity = userRepository.findByEmail(email);
        
        
        
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }
    
    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto    returnValue = new UserDto();
        UserEntity userEntity  = userRepository.findByUserId(userId);
        
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        
        BeanUtils.copyProperties(userEntity, returnValue);
        
        return returnValue;
    }
    
    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        UserDto    returnValue = new UserDto();
        UserEntity userEntity  = userRepository.findByUserId(userId);
        
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        UserEntity updatedUserDetails = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserDetails, returnValue);
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
    public List<UserDto> getUsers(int argPage, int argLimit) {
        List<UserDto> returnValue = new ArrayList<>();
        if (argPage >0) argPage--;
        Pageable      pageable    = PageRequest.of(argPage, argLimit);
        Page<UserEntity> usersPage = userRepository.findAll(pageable);
        List<UserEntity> users     = usersPage.getContent();
        users.stream().forEach(user -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            returnValue.add(userDto);
        });
        
        
        return returnValue;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
        
    }
    
    
}

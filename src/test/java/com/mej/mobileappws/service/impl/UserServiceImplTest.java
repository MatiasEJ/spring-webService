package com.mej.mobileappws.service.impl;

import com.mej.mobileappws.entity.UserEntity;
import com.mej.mobileappws.repository.UserRepository;
import com.mej.mobileappws.shared.dto.UserDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    
    public static final String EMAIL = "tet@12te.com";
    public static final String USER_ID ="wZGZe8cq39rmXhlN32ly9Rupo6Rw8y";
    public static final String FIRST_NAME ="digues";
    public static final String ENCRYPTED_PASS = "sasdaergey";
    UserEntity userEntity;
    
    @InjectMocks
    UserServiceImpl userService;
    
    @Mock
    UserRepository userRepository;
    
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userEntity = new UserEntity();
        userEntity.setId(1l);
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setUserId(USER_ID);
        userEntity.setEncryptedPassword(ENCRYPTED_PASS);
    }
    
    @Test
    void getUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        UserDto userDto = userService.getUser(EMAIL);
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(FIRST_NAME, userDto.getFirstName());
    }
    
    
    @Test()
    void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        
        Assertions.assertThrows(UsernameNotFoundException.class,()->{
            userService.getUser(EMAIL);
        });
    }
}
package com.mej.mobileappws.service.impl;

import com.mej.mobileappws.entity.AddressEntity;
import com.mej.mobileappws.entity.UserEntity;
import com.mej.mobileappws.repository.AddressRepository;
import com.mej.mobileappws.repository.UserRepository;
import com.mej.mobileappws.service.AddressService;
import com.mej.mobileappws.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
   
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    AddressRepository addressRepository;
    
    @Override
    public List<AddressDTO> getAddresses(String userId) {
        ModelMapper modelMapper = new ModelMapper();
       List<AddressDTO> returnValue = new ArrayList<>();
        UserEntity userEntity = userRepository.findByUserId(userId);
        
        if(userEntity == null) return returnValue;
       
        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
       for (AddressEntity addressEntity: addresses){
           returnValue.add(modelMapper.map(addressEntity,AddressDTO.class));
       }
        
       return returnValue;
    }
    
    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO returnValue = null;
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        if (addressEntity != null){
            returnValue = new ModelMapper().map(addressEntity,AddressDTO.class);
        }
        return returnValue;
    }
}

package com.mej.mobileappws.repository;

import com.mej.mobileappws.entity.AddressEntity;
import com.mej.mobileappws.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
    
    AddressEntity findByAddressId(String addressId);
}

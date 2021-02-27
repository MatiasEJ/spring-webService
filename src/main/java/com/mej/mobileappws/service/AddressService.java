package com.mej.mobileappws.service;

import com.mej.mobileappws.shared.dto.AddressDTO;
import com.mej.mobileappws.shared.dto.UserDto;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAddresses(String userId);
    AddressDTO getAddress(String addressId);
}

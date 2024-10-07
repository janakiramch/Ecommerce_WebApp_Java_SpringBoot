package com.dev.Ecommerce.service.interf;

import com.dev.Ecommerce.dto.AddressDto;
import com.dev.Ecommerce.dto.Response;

public interface AddressService {
    Response saveAndUpdateAddress(AddressDto addressDto);
}

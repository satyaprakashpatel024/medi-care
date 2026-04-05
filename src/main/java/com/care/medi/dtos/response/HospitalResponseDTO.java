package com.care.medi.dtos.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record HospitalResponseDTO(
     Long id,
     String name,
     String phone,
     Set<HospitalAddressResponseDTO> address,
     Set<String> departmentNames
){
}

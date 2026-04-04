package com.care.medi.dtos.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HospitalResponseDTO {
    private Long id;
    private String name;
    private String phone;
    private Set<HospitalAddressResponseDTO> address;
    private Set<String> departmentNames;
}

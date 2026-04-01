package com.care.medi.dtos;
import lombok.*;

import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HospitalResponse {
    private Integer id;
    private String name;
    private String phone;
    private AddressResponse address;
    private List<String> departmentNames;
}

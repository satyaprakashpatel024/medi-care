package com.care.medi.dtos;

import lombok.*;

import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DepartmentResponse {
    private Integer id;
    private String name;
    private List<String> hospitalNames;
}

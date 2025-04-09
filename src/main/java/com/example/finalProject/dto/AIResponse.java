package com.example.finalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
    private String message;
    private ResponseData data;
    private Button button;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ResponseData {
    private String doctorName;
    private String departmentName;
    private String branchName;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Button {
    private String text;
    private String url;
}

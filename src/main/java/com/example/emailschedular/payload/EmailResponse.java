package com.example.emailschedular.payload;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class EmailResponse {
    private boolean success;
    private String jobId;
    private String jobGroup;
    private String message;
}

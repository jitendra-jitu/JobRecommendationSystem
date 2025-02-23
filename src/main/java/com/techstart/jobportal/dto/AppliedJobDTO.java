package com.techstart.jobportal.dto;

import com.techstart.jobportal.model.Job;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AppliedJobDTO {
    private Job job;
    private Date applicationDate;
}

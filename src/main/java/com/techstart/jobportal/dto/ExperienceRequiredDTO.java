package com.techstart.jobportal.dto;

import lombok.Data;

@Data
public class ExperienceRequiredDTO {

    private int years;

    public ExperienceRequiredDTO(int years) {
        this.years = years;
    }
}

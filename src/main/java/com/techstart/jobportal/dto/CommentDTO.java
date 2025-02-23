package com.techstart.jobportal.dto;

import lombok.Data;

@Data
public class CommentDTO {

    private String text;
    private String author;
    private long date;
}

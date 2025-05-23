package com.methum.NewsApi.dtos;

import lombok.Data;

@Data
public class ResponseDto {

    private String title;
    private String author;
    private String SourceName;
    private String summary;
}

package com.methum.NewsApi.dtos;

import lombok.Data;

import java.util.List;

@Data
public class NewsResponseDto {

    private String status;
    private int totalResults;
    private List<ArticleDto> articles;

}

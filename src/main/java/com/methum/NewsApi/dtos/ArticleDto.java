package com.methum.NewsApi.dtos;

import lombok.Data;

@Data
public class ArticleDto {

    private SourceDto source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;
}

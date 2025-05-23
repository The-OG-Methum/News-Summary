package com.methum.NewsApi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class LlmArticleDto {

    private String name;
    private String author;
    private String title;
    private String content;
}

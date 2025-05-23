package com.methum.NewsApi.controller;


import com.methum.NewsApi.dtos.NewsResponseDto;
import com.methum.NewsApi.dtos.ResponseDto;
import com.methum.NewsApi.service.LlmService;
import com.methum.NewsApi.service.NewsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MainController {

    private final NewsService newsService;
    private final LlmService llmService;


    public MainController(NewsService newsService, LlmService llmService) {
        this.newsService = newsService;
        this.llmService = llmService;
    }

    @Value("${news.api.key}")
    private String apiKey;

    @GetMapping("/news")
    public ResponseEntity<List<ResponseDto>> getAllNews(@RequestParam String country) {

        NewsResponseDto NewsResponseDto = newsService.getAllNews(country, apiKey);

        ResponseEntity<List<ResponseDto>> responseDto = llmService.summarizeNews(NewsResponseDto);

        return responseDto;
    }



}

package com.methum.NewsApi.service;

import com.methum.NewsApi.dtos.ArticleDto;
import com.methum.NewsApi.dtos.LlmArticleDto;
import com.methum.NewsApi.dtos.NewsResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final RestTemplate restTemplate;

    public NewsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public NewsResponseDto getAllNews(String country,String apiKey) {

        String baseUrl = "https://newsapi.org/v2/top-headlines";

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("country",country)
                .queryParam("apiKey",apiKey)
                .toUriString();

        System.out.println(url);


        NewsResponseDto newsResponseDto = restTemplate.getForObject(url, NewsResponseDto.class);


        return newsResponseDto;
    }


}

package com.methum.NewsApi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.methum.NewsApi.dtos.ArticleDto;
import com.methum.NewsApi.dtos.NewsResponseDto;
import com.methum.NewsApi.dtos.OllamaRawResponse;
import com.methum.NewsApi.dtos.ResponseDto;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LlmService {

    private final RestTemplate restTemplate;
    private final String endpoint = "http://localhost:11434/api/generate";

    public LlmService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<List<ResponseDto>> summarizeNews(NewsResponseDto newsResponseDto) {
        if (newsResponseDto == null || newsResponseDto.getArticles() == null || newsResponseDto.getArticles().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<ArticleDto> articles = newsResponseDto.getArticles();
        List<ResponseDto> summaries = new ArrayList<>();

        // Limit to first 5 articles for performance
        List<ArticleDto> articlesToProcess = articles.stream()
                .limit(5)
                .toList();

        System.out.println("Processing " + articlesToProcess.size() + " articles for summarization...");

        // Process each article with progress indicator
        for (int i = 0; i < articlesToProcess.size(); i++) {
            ArticleDto article = articlesToProcess.get(i);
            try {
                System.out.println("Processing article " + (i + 1) + " of " + articlesToProcess.size() + ": " + article.getTitle());
                ResponseDto summary = summarizeArticle(article);
                if (summary != null) {
                    summaries.add(summary);
                    System.out.println("✓ Successfully summarized article " + (i + 1));
                } else {
                    System.out.println("✗ Failed to summarize article " + (i + 1));
                }
            } catch (Exception e) {
                System.err.println("✗ Failed to summarize article " + (i + 1) + ": " + article.getTitle());
                e.printStackTrace();
                // Continue processing other articles even if one fails
            }
        }

        if (summaries.isEmpty()) {
            System.out.println("No articles were successfully summarized.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        System.out.println("Completed processing. Successfully summarized " + summaries.size() + " out of " + articlesToProcess.size() + " articles.");
        return ResponseEntity.ok(summaries);
    }

    private ResponseDto summarizeArticle(ArticleDto article) {
        String prompt = "Summarize this article in 2-3 sentences:\n\n" +
                "Title: " + article.getTitle() + "\n" +
                (article.getContent() != null ? "Content: " + article.getContent().substring(0, Math.min(500, article.getContent().length())) : "") +
                "\n\nBrief summary:";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "phi");
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);  // Disable streaming for simpler response

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String response = responseEntity.getBody();
            System.out.println("Raw response for article '" + article.getTitle() + "':\n" + response);

            if (!responseEntity.getStatusCode().is2xxSuccessful() || response == null) {
                return null;
            }

            // With stream=false, we might still get NDJSON or a single JSON object
            String summary = extractSummaryFromResponse(response);

            if (summary.isEmpty()) {
                return null;
            }

            ResponseDto result = new ResponseDto();
            result.setTitle(article.getTitle());
            result.setAuthor(article.getAuthor());
            result.setSourceName(article.getSource() != null ? article.getSource().getName() : "Unknown");
            result.setSummary(summary);

            return result;

        } catch (Exception e) {
            System.err.println("Error processing article: " + article.getTitle());
            e.printStackTrace();
            return null;
        }
    }

    private String extractSummaryFromResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder summaryBuilder = new StringBuilder();

        try {
            // Try to parse as single JSON object first (when stream=false)
            OllamaRawResponse singleResponse = mapper.readValue(response, OllamaRawResponse.class);
            if (singleResponse.getResponse() != null) {
                return singleResponse.getResponse().trim();
            }
        } catch (Exception e) {
            // If single JSON parsing fails, try NDJSON parsing
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                try {
                    OllamaRawResponse chunk = mapper.readValue(line, OllamaRawResponse.class);
                    if (chunk.getResponse() != null) {
                        summaryBuilder.append(chunk.getResponse());
                    }
                } catch (Exception ex) {
                    System.err.println("Failed to parse line: " + line);
                }
            }
        }

        return summaryBuilder.toString().trim();
    }
}
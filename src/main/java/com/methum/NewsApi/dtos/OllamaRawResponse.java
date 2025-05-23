package com.methum.NewsApi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaRawResponse {

    private String response;    // summary
    private boolean done;
    private String model;
    private String created_at;
    private String done_reason;
}

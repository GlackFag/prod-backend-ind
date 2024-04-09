package com.glackfag.travelgentle.maps;

import com.glackfag.travelgentle.util.GigaChatProxy;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SightSuggester {
    @Value("${sightexamples}")
    private String sightSystemPrompt;
    @Value("${sight}")
    private String suggestTemplate;
    @Value("${recognize}")
    private String recognizeTemplate;
    @Value("${recognizeexample}")
    private String recognizeSystemPrompt;

    private final GigaChatProxy gigaChatProxy;

    @Autowired
    public SightSuggester(GigaChatProxy gigaChatProxy) {
        this.gigaChatProxy = gigaChatProxy;
    }

    @PostConstruct
    private void format() {
        sightSystemPrompt = sightSystemPrompt.substring(1, sightSystemPrompt.length() - 1);
        suggestTemplate = suggestTemplate.substring(1, suggestTemplate.length() - 1);
        recognizeTemplate = recognizeTemplate.substring(1, recognizeTemplate.length() - 1);
        recognizeSystemPrompt = recognizeSystemPrompt.substring(1, recognizeSystemPrompt.length() - 1);
    }

    @SneakyThrows
    public String suggest(String city, String[] ignoredSights) {
        String ignored = String.join(";", ignoredSights);
        String prompt = String.format(suggestTemplate, city, ignored);

        String response = gigaChatProxy.executePrompt(prompt, sightSystemPrompt);

        return GigaChatProxy.retrieveMessageContent(response);
    }

    public String getSuggestedSight(String content) {
        String response = gigaChatProxy.executePrompt(String.format(recognizeTemplate, content), recognizeSystemPrompt);

        return GigaChatProxy.retrieveMessageContent(response);
    }
}

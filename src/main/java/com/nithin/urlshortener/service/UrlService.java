package com.nithin.urlshortener.service;
import com.nithin.urlshortener.model.Url;
import com.nithin.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;
    public String shortenUrl(String originalUrl) {
        String shortCode = UUID.randomUUID().toString().substring(0, 8);
        Url url = new Url(shortCode, originalUrl, 0);
        urlRepository.save(url);
        return shortCode;
    }
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));
        return url.getOriginalUrl();
    }

}

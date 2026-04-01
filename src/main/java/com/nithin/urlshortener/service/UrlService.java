package com.nithin.urlshortener.service;
import com.nithin.urlshortener.exception.ResourceNotFoundException;
import com.nithin.urlshortener.model.Url;
import com.nithin.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    public String shortenUrl(String originalUrl) {
        String shortCode = UUID.randomUUID().toString().substring(0, 8);
        Url url = new Url(shortCode, originalUrl, 0);
        urlRepository.save(url);
        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        String originalUrl = redisTemplate.opsForValue().get(shortCode);
        if(originalUrl!=null) {
            return originalUrl;
        }
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));
        redisTemplate.opsForValue().set(shortCode , url.getOriginalUrl());
        return url.getOriginalUrl();
    }

}

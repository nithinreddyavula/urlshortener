package com.nithin.urlshortener.service;
import com.nithin.urlshortener.exception.ResourceNotFoundException;
import com.nithin.urlshortener.model.Url;
import com.nithin.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        if (originalUrl != null) {
            return originalUrl;
        }

        redisTemplate.opsForValue().set(shortCode, url.getOriginalUrl(), 10, TimeUnit.MINUTES);
        return url.getOriginalUrl();
    }
    public int getClickCount(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));
        return url.getClickCount();
    }
}

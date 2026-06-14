package com.nithin.urlshortener.service;

import com.nithin.urlshortener.exception.ResourceNotFoundException;
import com.nithin.urlshortener.model.Url;
import com.nithin.urlshortener.repository.UrlRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BASE62 =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String encodeBase62(long num) {

        if (num == 0) {
            return "a";
        }

        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            sb.append(BASE62.charAt((int) (num % 62)));
            num /= 62;
        }

        return sb.reverse().toString();
    }

    public String shortenUrl(String originalUrl) {

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setClickCount(0);

        // First save to get auto-generated ID
        url = urlRepository.save(url);

        String shortCode = encodeBase62(url.getId());

        url.setShortCode(shortCode);
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

        redisTemplate.opsForValue()
                .set(shortCode, url.getOriginalUrl(), 10, TimeUnit.MINUTES);

        return url.getOriginalUrl();
    }

    public int getClickCount(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        return url.getClickCount();
    }
}

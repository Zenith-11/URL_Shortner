package com.urlshortener.service;

import com.urlshortener.Dto.CreateShortUrlRequest;
import com.urlshortener.Dto.CreateShortUrlResponse;
import com.urlshortener.model.ShortUrl;
import com.urlshortener.repository.ShortUrlRepository;
import com.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;

@Service
public class UrlShortnerService {

    private final ShortUrlRepository shortUrlRepository;
    private final StringRedisTemplate redisTemplate;


    public UrlShortnerService(ShortUrlRepository shortUrlRepository,  StringRedisTemplate redisTemplate) {
        this.shortUrlRepository = shortUrlRepository;
        this.redisTemplate = redisTemplate;
    }

    public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request){
        String shortcode ;

        do{
            shortcode = ShortCodeGenerator.generate();
        }while(shortUrlRepository.existsByShortCode(shortcode));

        ShortUrl shortUrl = new ShortUrl();

     shortUrl.setShortCode(shortcode);
     shortUrl.setLongUrl(request.getLongUrl());
     shortUrl.setCreatedAt(LocalDateTime.now());
     shortUrl.setClickCount(0L);

     if(request.getExpiryDays()!= null){
         shortUrl.setExpiresAt(LocalDateTime.now().plusDays(request.getExpiryDays()));
     }

     shortUrlRepository.save(shortUrl);

        return new CreateShortUrlResponse("http://localhost:8080/" + shortcode);
    }

    public String resolveShortUrl(String shortCode) {

        String cacheKey = "shorturl:" + shortCode;
        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUrl != null) {
            return cachedUrl; // 🚀 fast path
        }

        ShortUrl shortUrl = shortUrlRepository
                .findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        // check expiry
        if (shortUrl.getExpiresAt() != null &&
                shortUrl.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Short URL expired");
        }

        redisTemplate.opsForValue()
                .set(cacheKey, shortUrl.getLongUrl());

        // increment click count
        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepository.save(shortUrl);

        return shortUrl.getLongUrl();
    }



}

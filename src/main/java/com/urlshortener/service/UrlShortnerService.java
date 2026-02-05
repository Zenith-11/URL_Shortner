package com.urlshortener.service;

import com.urlshortener.Dto.CreateShortUrlRequest;
import com.urlshortener.Dto.CreateShortUrlResponse;
import com.urlshortener.model.ShortUrl;
import com.urlshortener.repository.ShortUrlRepository;
import com.urlshortener.util.ShortCodeGenerator;

import java.time.LocalDateTime;

public class UrlShortnerService {

    private final ShortUrlRepository shortUrlRepository;

    public UrlShortnerService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
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
}

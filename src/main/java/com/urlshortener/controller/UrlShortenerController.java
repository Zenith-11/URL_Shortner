package com.urlshortener.controller;


import com.urlshortener.Dto.CreateShortUrlRequest;
import com.urlshortener.Dto.CreateShortUrlResponse;
import com.urlshortener.service.RateLimitService;
import com.urlshortener.service.UrlShortnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import com.urlshortener.exception.RateLimitExceededException;

@RestController
@RequestMapping("/api/v1")
public class UrlShortenerController {

    private final UrlShortnerService urlShortenerService;
    private final RateLimitService rateLimitService;

    public UrlShortenerController(UrlShortnerService urlShortenerService,
                                  RateLimitService rateLimitService){
        this.urlShortenerService = urlShortenerService;
        this.rateLimitService = rateLimitService;
    }


    @PostMapping("/shorten")
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request,
            HttpServletRequest httpRequest
    ){

        String ip = httpRequest.getRemoteAddr();
        String key = "rate:" + ip + ":shorten";

        if (!rateLimitService.isAllowed(key, 5, 120)) {
            throw new RateLimitExceededException("Too many shorten requests");
        }

        CreateShortUrlResponse response = urlShortenerService.createShortUrl(request);

      return new ResponseEntity<>(response , HttpStatus.CREATED);
    }


    @GetMapping("/{shortCode}")
    public void redirect(
            @PathVariable String shortCode,
            HttpServletResponse response,
            HttpServletRequest request

    ) throws IOException {

        String ip = request.getRemoteAddr();
        String key = "rate:" + ip + ":redirect";

        if (!rateLimitService.isAllowed(key, 10, 60)) {
            throw new RateLimitExceededException("Too many redirect requests");
        }


        String longUrl = urlShortenerService.resolveShortUrl(shortCode);

        response.sendRedirect(longUrl); // sends HTTP 302
    }


}

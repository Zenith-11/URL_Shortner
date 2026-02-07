package com.urlshortener.controller;


import com.urlshortener.Dto.CreateShortUrlRequest;
import com.urlshortener.Dto.CreateShortUrlResponse;
import com.urlshortener.service.UrlShortnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class UrlShortenerController {

    private final UrlShortnerService urlShortenerService;

    public UrlShortenerController(UrlShortnerService urlShortenerService){
        this.urlShortenerService = urlShortenerService;
    }


    @PostMapping("/shorten")
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request){
            CreateShortUrlResponse response = urlShortenerService.createShortUrl(request);

      return new ResponseEntity<>(response , HttpStatus.CREATED);
    }


    @GetMapping("/{shortCode}")
    public void redirect(
            @PathVariable String shortCode,
            HttpServletResponse response
    ) throws IOException {

        String longUrl = urlShortenerService.resolveShortUrl(shortCode);

        response.sendRedirect(longUrl); // sends HTTP 302
    }


}

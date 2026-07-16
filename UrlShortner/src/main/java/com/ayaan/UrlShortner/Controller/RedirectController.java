package com.ayaan.UrlShortner.Controller;

import com.ayaan.UrlShortner.Entity.UrlEntity;
import com.ayaan.UrlShortner.Service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


@RestController
public class RedirectController {

        private final UrlService urlService;

        public RedirectController(UrlService urlService) {
            this.urlService = urlService;
        }

        @GetMapping("/{shortCode}")
        public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
            UrlEntity entity = urlService.getActiveUrlOrThrow(shortCode);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(entity.getUrl()))
                    .build();
        }
    }
}

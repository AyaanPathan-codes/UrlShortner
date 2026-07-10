package com.ayaan.UrlShortner.Repo;

import com.ayaan.UrlShortner.Entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepo extends JpaRepository<UrlEntity, Long> {
    boolean existsByShortUrl(String shortUrl);
    Optional<UrlEntity> findByShortUrl(String shortUrl);
}

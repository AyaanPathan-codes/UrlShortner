package com.ayaan.UrlShortner.Repo;

import com.ayaan.UrlShortner.Entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UrlRepo extends JpaRepository<UrlEntity, Long> {
    boolean existsByShortUrl(String shortUrl);
    Optional<UrlEntity> findByShortUrl(String shortUrl);

    @Modifying
    @Query("UPDATE UrlEntity u SET u.clickCount = u.clickCount + 1 WHERE u.shortUrl = :shortCode")
    void incrementClickCount(@Param("shortCode") String shortCode);
}

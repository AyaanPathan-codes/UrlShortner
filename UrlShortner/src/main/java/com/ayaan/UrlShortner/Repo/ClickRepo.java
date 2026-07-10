package com.ayaan.UrlShortner.Repo;

import com.ayaan.UrlShortner.Entity.Click;
import com.ayaan.UrlShortner.Entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClickRepo extends JpaRepository<Click,Long> {
    long countByUrl(UrlEntity url);
    List<Click> findByUrlAndClickedAtBetween(UrlEntity url, LocalDateTime start, LocalDateTime end);
}

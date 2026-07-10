package com.ayaan.UrlShortner.Repo;

import com.ayaan.UrlShortner.Entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepo extends JpaRepository<UrlEntity, Long> {
}

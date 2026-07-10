package com.ayaan.UrlShortner.Repo;

import com.ayaan.UrlShortner.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<Users,Long> {
}

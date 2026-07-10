package com.ayaan.UrlShortner.Repo;

import com.ayaan.UrlShortner.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users,Long> {
    Optional<Users> findByEmail(String email);
}

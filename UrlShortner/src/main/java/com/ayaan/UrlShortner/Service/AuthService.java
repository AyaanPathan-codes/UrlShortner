package com.ayaan.UrlShortner.Service;

import com.ayaan.UrlShortner.Dto.LoginRequest;
import com.ayaan.UrlShortner.Dto.LoginResponse;
import com.ayaan.UrlShortner.Dto.RegisterRequest;
import com.ayaan.UrlShortner.Entity.Enums.PlanType;
import com.ayaan.UrlShortner.Entity.Enums.Role;
import com.ayaan.UrlShortner.Entity.Enums.UserStatus;
import com.ayaan.UrlShortner.Entity.Users;
import com.ayaan.UrlShortner.Exceptions.CustomExceptions;
import com.ayaan.UrlShortner.Repo.UsersRepo;
import com.ayaan.UrlShortner.Entity.CustomUserDetails;
import com.ayaan.UrlShortner.Security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsersRepo usersRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.usersRepo = usersRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse register(RegisterRequest request) {

        if (usersRepo.findByEmail(request.email()).isPresent()) {
            throw new CustomExceptions.DuplicateEmailException(
                    "An account with this email already exists");
        }

        Users user = new Users();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setPlanType(PlanType.FREE);
        user.setStatus(UserStatus.ACTIVE);

        usersRepo.save(user);

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new LoginResponse(token,user.getEmail(),user.getPlanType().name());
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        Users user = usersRepo.findByEmail(request.email())
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException(
                        "User not found"));

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new LoginResponse(token,user.getEmail(),user.getPlanType().name());
    }
}
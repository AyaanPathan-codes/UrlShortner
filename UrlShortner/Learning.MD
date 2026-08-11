# Interview Questions — URL Shortener Project

Organized by topic. Each section goes basic → advanced. Practice explaining out loud, not just reading.

---

## 1. System Design / High-Level

**Basic**
- Walk me through the full request lifecycle when a user creates a short URL.
- Walk me through what happens when someone clicks a short link.
- Why did you choose a random string over a hash-based or sequential ID approach for short codes?
- What's the difference between a 301 and 302 redirect, and which did you use — why?

**Intermediate**
- How would you scale this to handle 1 million requests/day? 1 million/second?
- Where are the bottlenecks in your current design?
- If the redirect endpoint gets 100x more traffic than URL creation, how does your architecture already account for that? (Redis caching)
- How would you shard the database if a single Postgres instance couldn't handle the write volume?

**Advanced**
- Design this system from scratch for a company like Bitly — what would you change at massive scale?
- How would you handle a "hot key" problem — one extremely popular short link getting millions of clicks?
- What's the tradeoff between consistency and availability in your caching layer? Which did you choose (CAP theorem)?

---

## 2. Concurrency & Race Conditions

**Basic**
- What is a race condition? Give an example from your project.
- Why isn't checking `existsByShortUrl()` before saving enough to prevent duplicates?

**Intermediate**
- Explain exactly how your two-layer collision protection works (app check + DB constraint).
- Why do you catch `DataIntegrityViolationException` specifically, and what does the database do to trigger it?
- What would happen if you removed the `unique = true` constraint from `shortUrl` — walk through the exact failure scenario with two concurrent requests.
- Why is `UPDATE urls SET click_count = click_count + 1` safer under concurrency than "fetch entity, increment, save"?

**Advanced**
- What isolation level does your database use by default, and how does it relate to this race condition?
- How would optimistic locking (`@Version`) be a different solution to the same problem — what are the tradeoffs vs. your current approach?
- At what point does the retry-loop approach break down under extremely high contention, and what would you do differently at that scale?

---

## 3. Authentication & JWT

**Basic**
- What is JWT, and what problem does it solve?
- What's inside a JWT? Is it encrypted?
- What's the difference between authentication and authorization?
- Why do you use `BCryptPasswordEncoder` instead of storing plaintext or using a faster hash like MD5?

**Intermediate**
- Walk me through what `JwtFilter` does on every request.
- Why does your filter re-fetch the user from the database instead of trusting the role embedded in the token's claims?
- What's the security implication of that design choice — what does it protect against?
- What happens if a JWT is stolen? How would you mitigate that (token expiry, revocation)?
- Why is `CustomUserDetails` needed instead of using Spring Security's built-in `User` class?

**Advanced**
- JWTs are stateless — what's the actual mechanism for revoking one before it expires? How would you implement that (Redis blacklist)?
- Compare JWT-based auth to session-based (cookie) auth — tradeoffs in scalability, security, and complexity.
- Why is CSRF protection disabled in your `SecurityConfig`, and why is that the correct choice here (not a shortcut)?
- How would refresh tokens change your architecture, and why might you want them?

---

## 4. Caching (Redis)

**Basic**
- What is caching, and why did you add it specifically to the redirect endpoint and not URL creation?
- What is the Cache-Aside pattern? Does your code implement it?

**Intermediate**
- Why can't you cache the `UrlEntity` object directly — what's the actual failure mode?
- What is a Hibernate lazy-loading proxy, and why does it break serialization?
- Why did you create a separate `CachedUrlDto` instead of just caching the entity?
- What is TTL, and why did you add one even though you already have explicit `@CacheEvict` calls?

**Advanced**
- What's the difference between Cache-Aside, Write-Through, and Write-Behind caching strategies? Why did you choose Cache-Aside?
- What happens if Redis goes down — does your application still function? What's the failure mode?
- How would you handle a cache stampede (many requests missing cache simultaneously for the same hot key)?
- Explain cache invalidation strategy: why evict-on-write rather than update-cache-on-write?

---

## 5. Database Design & JPA/Hibernate

**Basic**
- Walk me through your entity relationships (Users, UrlEntity, Click).
- Why did you use enums with `EnumType.STRING` instead of storing plain strings or using `ORDINAL`?
- What's the difference between `@OneToMany` and `@ManyToOne` in your schema?

**Intermediate**
- Why did you choose `FetchType.LAZY` for the `Users` relationship on `UrlEntity`? What's the tradeoff vs. `EAGER`?
- What is the N+1 query problem, and could it occur anywhere in your current design?
- Why did you avoid using Lombok's `@Data` on entities, and use `@Getter/@Setter` instead?
- What is `ddl-auto: update`, and why is it inappropriate for production? What would you use instead (Flyway/Liquibase)?

**Advanced**
- How would you design a schema migration strategy for zero-downtime deployment?
- What indexes would you add to this schema, and why (e.g., on `short_url`, `user_id`, `email`)?
- How would you partition the `Click` table if it grew to billions of rows?

---

## 6. Rate Limiting

**Basic**
- What is rate limiting, and why is it needed?
- What algorithm does Bucket4j use (token bucket)? Explain how it works conceptually.

**Intermediate**
- Why is your current rate limiter keyed per-user rather than per-IP?
- What's the limitation of storing buckets in a `ConcurrentHashMap`? Why does this break under horizontal scaling?
- How would Redis-backed rate limiting solve that problem?

**Advanced**
- Compare token bucket, leaky bucket, and fixed/sliding window rate limiting algorithms — tradeoffs of each.
- How would you rate-limit by multiple dimensions simultaneously (per-user AND per-IP AND global)?
- Where should rate limiting ideally live in a distributed system — application layer, API gateway, or both? Why?

---

## 7. Payments (Razorpay)

**Basic**
- Why do you verify the webhook's signature before trusting it?
- Why can't the frontend alone be trusted to tell your backend "payment succeeded"?

**Intermediate**
- Walk me through your `PaymentOrder` entity — why is it necessary at all?
- What does idempotency mean, and where do you handle it in your webhook logic? Why does it matter (webhooks can fire more than once)?
- Why is the payment amount hardcoded server-side rather than accepted from the client request?

**Advanced**
- What would change if you needed true recurring billing instead of one-time payments?
- How would you handle a webhook that arrives out of order, or a payment that's later refunded?
- What's your reconciliation strategy if Razorpay's records and your database ever disagree?

---

## 8. Authorization / Admin / RBAC

**Basic**
- How does `@PreAuthorize("hasRole('ADMIN')")` actually work under the hood?
- Why is there no public endpoint to create an admin account?

**Intermediate**
- Explain the full chain: how does a `Role` enum value on `Users` eventually become something `@PreAuthorize` can check?
- Why is `@EnableMethodSecurity` required for `@PreAuthorize` to function at all?
- Why does admin get read-only visibility into payments rather than the ability to manually grant premium?

**Advanced**
- How would you design fine-grained permissions (beyond just USER/ADMIN roles) if requirements grew more complex?
- What's the security risk of a compromised admin account in your current design, and how would you mitigate it (audit logging, 2FA)?

---

## 9. Docker & DevOps

**Basic**
- What's the difference between a Docker image and a container?
- Why does your Dockerfile use a multi-stage build?

**Intermediate**
- How do containers communicate with each other in Docker Compose (service names as hostnames)?
- Why does `depends_on` alone not guarantee Postgres is ready — what does the healthcheck actually solve?
- Why is `.env` excluded from Git but not from the app's runtime behavior?

**Advanced**
- How would you deploy this with zero-downtime updates?
- What would change about your Docker setup if you needed to run multiple app instances behind a load balancer?
- How do you handle secrets in a real production environment vs. your current `.env` approach?

---

## 10. Error Handling & API Design

**Basic**
- What does `@RestControllerAdvice` do, and why is it better than letting exceptions propagate raw?
- Why do you return different HTTP status codes for different exceptions (409 vs 404 vs 403)?

**Intermediate**
- Why do you return a generic "not found" message for both disabled and truly nonexistent links, rather than being specific?
- Why did you separate request and response DTOs instead of reusing one class?
- Why is a Java `record` a good fit for DTOs specifically?

**Advanced**
- How would you version this API if you needed to make a breaking change without disrupting existing clients?
- What's your strategy for API documentation (OpenAPI/Swagger) — do you have any, and what would you add?

---

## 11. General / Behavioral (project-based)

- What was the hardest bug you hit building this, and how did you debug it?
- Tell me about a time your assumption about how something worked turned out to be wrong. (e.g., the `ddl-auto` drift bug, or the `@AuthenticationPrincipal` type mismatch)
- If you had another month, what would you build next and why?
- What would you do differently if you started this project over?
- Walk me through a design decision you made and later changed your mind about (e.g., link expiry not being plan-gated).

---

## How to actually prepare with this list
1. Don't memorize answers — be able to **explain the reasoning**, since follow-ups will probe "why," not just "what"
2. For every bug you actually hit (there's a long real history in your build notes), be ready to explain root cause and fix — these make excellent, credible answers to "tell me about a bug you fixed"
3. Practice the concurrency and caching sections hardest — these come up most often and test real understanding, not memorized facts
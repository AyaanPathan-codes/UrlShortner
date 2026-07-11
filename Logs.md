1. We used SecureRandom not Random becuase in random it is not thread safe or anyone can just read it or decode it easily but SecureRandom is more secured as it uses cryptographic methods to Secure our Generated Short Code

2. Race Condition / Concurrency in Short Code Generation:
Problem: Two requests could hit createShortUrlSafely() at the exact same time.
Both check existsByShortUrl("abc123") → both get false (neither exists yet)
Both then try to save() with the same code → without protection, this
would corrupt data (two rows, same short_url, pointing to different long URLs).

Fix - two layers:
a) App-level check (existsByShortUrl) - cheap, catches it in the common case,
but has a gap between "check" and "save" where another request can slip in.
b) DB-level unique constraint (unique = true on shortUrl column) - this is the
REAL guarantee. If two inserts race past the app-level check, the database
itself rejects the second insert and throws DataIntegrityViolationException.

We catch that exception in the service and retry with a new random code
(only for random codes - if it was a custom alias, we throw DuplicateAliasException
back to the user instead of silently retrying, since that's a user choice, not
something we should auto-fix for them).

Key insight: app-level checks are optimizations, DB constraints are guarantees.
Never rely only on a check-then-write pattern in the app layer when concurrent
requests are possible.


What's correct in what you said

Register: user sends email/password → server hardcodes role: USER, planType: FREE, status: ACTIVE → password is hashed → user saved
Login: credentials get cross-checked against what's stored in the DB
Password comparison happens via the encoder, not a plain equality check
After login/register, client holds a Bearer token and must send it on subsequent requests, or gets 403
Create URL requires that Bearer token

The mix-up: JwtFilter role
JwtFilter has exactly one job: validate a token that's already been sent on an incoming request. It runs on every request (it's a global filter), but during register/login there's no token yet to validate — so on those two requests, it just does nothing and passes through.
Registration flow, correctly:
Client → AuthController.register()
→ AuthService.register()
- checks email not already taken
- hashes password
- saves Users (role=USER, planType=FREE, status=ACTIVE)
- calls JwtService.generateToken(new CustomUserDetails(user))
→ returns token to client
JwtFilter was never called meaningfully here — it's not part of generating a token, only validating one on later requests.
Login flow, correctly:
Client → AuthController.login()
→ AuthService.login()
- authenticationManager.authenticate(email, password)
→ internally calls CustomUserDetailsService.loadUserByUsername(email)
→ compares raw password against passwordHash using BCryptPasswordEncoder
→ throws BadCredentialsException if wrong → your 403
- if valid: calls JwtService.generateToken(...)
→ returns token to client
Again — JwtFilter not involved. It's still not validating anything, because there's no token being sent on this request; a token is being created as the output.
Create URL flow — THIS is where JwtFilter actually runs and matters:
Client → sends request with Authorization: Bearer <token>
→ JwtFilter intercepts BEFORE the controller
- extracts token from header
- JwtService.extractEmail(token) + isTokenValid(token, userDetails)
- if valid: loads user, puts Authentication into SecurityContextHolder
- if invalid/missing: request continues unauthenticated → hits "authenticated()" rule → 403
→ UrlController.createUrl() — now @AuthenticationPrincipal has something to give you
The one-sentence fix to remember
Token generation (register/login) and token validation (JwtFilter, every subsequent request) are two completely separate code paths that never call each other. Generation happens once, inside AuthService, using JwtService.generateToken(). Validation happens on every later request, inside JwtFilter, using JwtService.extractEmail()/isTokenValid().
This is genuinely a common point of confusion, so good that you tried to narrate it — that's exactly how this kind of gap surfaces before an interview does it for you.
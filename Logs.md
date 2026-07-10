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
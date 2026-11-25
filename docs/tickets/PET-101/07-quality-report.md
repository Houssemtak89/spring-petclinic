# PET-101 · Quality report

| Check | Result |
|---|---|
| Build and tests (`mvnw -B verify`, incl. spring-javaformat and checkstyle) | passed, 109 tests, 0 failures, 2 skipped (MySQL/Postgres Testcontainers: Docker not installed). Last run after the F2 tests were strengthened in step 8; first run had 89 tests |
| Coverage | 93.8% of lines (threshold 80%, plugin default) |
| Sonar quality gate | not configured for this project |
| Conventions review | 0 must fix, 0 should fix, 2 notes (left as is) |

## Findings fixed
| Where | Finding | Fix |
|---|---|---|
| — | none | — |

Notes kept for the developer (`06-log/review-conventions.md`):
- The new tests use `@DisplayName` with "Fn: ..." plus `fN_` method names; the rest of the suite uses neither. Accepted: the workflow asks test names to cite the rule.
- The null date is guarded both in `Pet.hasVisitOn` and in the controller. Kept as defensive code.

## What failed along the way
- Step 6, first full build: `system/I18nPropertiesSyncTest.checkI18nPropertyFilesAreInSync` failed because the new key was only in `messages.properties` (the plan said "English only"). Cause: an existing test enforces that every key is in every locale bundle, which the understanding and the plan missed. The developer chose to copy the English sentence into the 9 locale bundles (plan, answer 7).
- Step 6: a past date that was also taken showed two errors on the date field; the developer chose to show only the future-date error (F6, answer 8). A failing test came first, then the fix.
- Step 8: the functional review rated F2 "weak" (only the default bundle was checked, nothing on the rendered page). The test-engineer added checks on every bundle and on the rendered form (also with `?lang=de`); the quality gates were run again and passed.
- Main session tooling: a `sed -i` over the CRLF locale bundles in Git Bash stripped the line endings and inserted nothing; the files were restored from git and the line was added with a byte-safe `perl` substitution.

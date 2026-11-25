# Remote review · java-code-reviewer · Pet.java, VisitController.java

Service: Java Code Reviewer running locally (`http://localhost:7860`), model `nvidia/nemotron-3-ultra-550b-a55b:free` through OpenRouter.

Sent: `Pet.java` (101 lines). Not sent: nothing withheld.
Unavailable: `VisitController.java` (118 lines) — the model did not answer within 60 seconds, twice. `Pet.java` answered on the second attempt (55 s).

Lines changed by PET-101 in `Pet.java`: 85 to 99 (`hasVisitOn`).

| # | Severity given | Where | Finding | Checked how | Kept |
|---|---|---|---|---|---|
| 1 | MAJOR / performance | `Pet.java:56` | `visits` is fetched eagerly | True of the code, but line 56 was not changed by this ticket, and the eager fetch is an existing choice of the project | dropped: outside the ticket |
| 2 | MAJOR / design | `Pet.java:77` | `getVisits()` returns the mutable collection | True of the code, not changed by this ticket; `VisitController` and the tests rely on it | dropped: outside the ticket |
| 3 | MAJOR / design | `Pet.java:81` | `addVisit` does not set the back-reference on `Visit` | Wrong: the relation is one-way (`@JoinColumn(name = "pet_id")` on `Pet`); `Visit` has no `pet` field and no `setPet` | dropped: not true |
| 4 | MINOR / bug | `Pet.java:97` | `hasVisitOn` may throw a NullPointerException | The finding argues itself out of it: `date` is checked for null at line 94 and `date.equals(null)` is false. No defect | dropped: not a defect |

Confirmed findings: 0. Dropped after checking: 4 (2 outside the ticket, 1 not true, 1 not a defect).

Nothing goes back to an owner. The line numbers given by the service were exact.

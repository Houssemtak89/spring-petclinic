# PET-101 · Fast plan

## Agreed behaviour
- F1: When a new visit is booked for a pet that already has a visit on the same calendar date, the booking is refused: the visit form is shown again, the error appears on the date field, and nothing is saved.
- F2: The error message is exactly "This pet already has a visit on this date." (English only).
- F3: A visit for the same pet on a different date is accepted as today.
- F4: A visit on a date where only another pet has a visit is accepted (the rule is per pet).
- F6: When the date is not in the future, only "Visit date must be in the future" is shown; the duplicate check runs only on a date that passed the future-date check (added during step 6, developer's answer below).
- F5: The rule applies to the creation of a visit only. Existing duplicates in the database are left alone; there is no database constraint (best effort against simultaneous bookings).

## Open points
| # | Question | Developer's answer |
|---|---|---|
| 1 | Is "same day" strictly the calendar date? | "strictly the calendar date for the same pet, no exception; two different pets on the same date is fine." |
| 2 | Message wording and languages | "the message is 'This pet already has a visit on this date.', English only." |
| 3 | Form check or database guarantee? | "a form check is enough, best effort for simultaneous bookings, no database constraint." |
| 4 | Existing duplicates | "not applicable, leave existing duplicates alone." |
| 5 | Scope | "creation only, nothing else." |
| 7 | (step 6) The existing `I18nPropertiesSyncTest` requires every key in all locale bundles; how to keep "English only"? | "copy the same English sentence into the 9 language files." |
| 8 | (step 6) A past date that is also taken showed two errors | "yes, only the first message; run the duplicate check only when the date passed the future-date check. No need to approve again, record it in the plan." |
| 6 | Test-first for this ticket? | "test-first: yes, write the failing tests before the code" (given with /approve fast). |

## Technical choices
- Rule in the model: a small query method on `Pet` (`src/main/java/org/springframework/samples/petclinic/owner/Pet.java`), for example `hasVisitOn(LocalDate date, Visit ignored)`, that returns true when another visit of the pet has that date. It must skip the visit being created, because `VisitController.loadPetWithVisit` already adds it to the pet (`VisitController.java:79`); `Visit` has no `equals`, so identity comparison is used.
- Check in `VisitController.processNewVisitForm` (`VisitController.java:97-112`), right after the existing future-date check, with the same pattern: `result.rejectValue("date", "duplicate.visitDate")`. The template already shows field errors on `date`, so it does not change.
- Message: new key `duplicate.visitDate=This pet already has a visit on this date.` in `src/main/resources/messages/messages.properties` and, with the same English sentence, in the 9 locale bundles `messages_{de,es,fa,hi,ja,ko,pt,ru,tr}.properties` (answers 2 and 7; the existing `I18nPropertiesSyncTest` stays as is).
- The duplicate check runs only when the date passed the future-date check (answer 8, F6).
- No schema change, no new exception, no transaction change (answers 3 and 4).
- Test-first: yes (open point 6). T2 runs first and its tests must fail for the right reason before T1 starts.

## Tests by rule
| Rule | Level | Test class |
|---|---|---|
| F1 | Spring MVC slice (`@WebMvcTest`): field error code `duplicate.visitDate` on `date`, form view, `owners.save` never called | `VisitControllerTests` |
| F2 | Unit check that the key resolves to the agreed text in the default bundle (or assertion on the rendered page) | `VisitControllerTests` |
| F3 | MVC slice: pet with a visit on another date, booking redirects | `VisitControllerTests` |
| F4 | MVC slice: another pet of the owner has a visit on that date, booking redirects | `VisitControllerTests` |
| F1, F3 | Unit: `Pet.hasVisitOn` true / false, ignores the visit passed as ignored | `PetTests` (new, `src/test/java/org/springframework/samples/petclinic/owner/PetTests.java`) |

## Tasks
| # | Who | Files | Rules |
|---|---|---|---|
| T1 | `developer` | `Pet.java`, `VisitController.java`, `messages/messages.properties` | F1, F2, F3, F4 |
| T2 | `test-engineer` | `VisitControllerTests.java`, `PetTests.java` (new) | F1, F2, F3, F4 |
| T3 | `test-engineer` (test first) | `VisitControllerTests.java` | F6 |
| T4 | main session | `VisitController.java`, `messages_{de,es,fa,hi,ja,ko,pt,ru,tr}.properties` | F2, F6 |

Test-first: T2 runs first and its tests are seen failing; then T1 makes them pass. Both use the method name and message key given above.

## Checks
`./mvnw -B verify` (build, tests, spring-javaformat, checkstyle); JaCoCo report on the changed lines (no threshold is configured in `pom.xml`); conventions review lens on the diff; functional review of F1-F5.

# Review · conventions · whole diff of PET-101

| # | Severity | Where | Finding | Evidence | Fix | Owner |
|---|---|---|---|---|---|---|
| 1 | note | `VisitControllerTests.java:143,160,173,189,205` and `PetTests.java` (all 5 tests) | New tests use `@DisplayName` plus snake-style names (`f1_...`). No other test in `src/test` uses `@DisplayName`, and the existing tests in the same class are camelCase (`processNewVisitFormSuccess`). The rule is cited twice (name and display name). | `grep DisplayName src/test` finds only these two files. Lens: test names must cite the `F` rule, so this is acceptable. It is a second style for the same job. | Optional: keep the `fN_` prefix in the method name and drop `@DisplayName`, or keep as is. No action required. | T2 / T3 |
| 2 | note | `Pet.java:95-97` and `VisitController.java:103` | The null date is guarded twice: in `hasVisitOn` (`date == null`) and in the controller (`visit.getDate() != null`). The `Pet` guard has no caller that needs it, and it is not part of the rules F1-F6. | `02-fast-plan.md` technical choices describe the method without a null-date case. YAGNI. | Remove the `date == null` branch in `Pet.hasVisitOn`, or leave it as defensive code and accept the redundancy. | T1 |

Checked, nothing to report:
- Plan conformity: `Pet.hasVisitOn(LocalDate, Visit)` with an identity comparison (as in the plan). The check sits right after the future-date check and uses `result.rejectValue("date", "duplicate.visitDate")`, the same pattern. The `else if` carries F6. No schema change, no new exception, no transaction change, no new library.
- Messages: the same key and the same English sentence in all 10 bundles (one added line each, diff confirms, encoding of the existing lines untouched). This follows answers 2 and 7.
- Tests: F1, F2, F3, F4, F6 are covered in `VisitControllerTests`. `PetTests` covers F1 and F3 as planned. The diff of `VisitControllerTests` only adds lines. The `init()` refactor to fields is needed by the new tests, and the existing tests are not weakened or disabled. No unused imports. No commented-out code, TODO or debug output.
- Scope: no reformatting or drive-by change outside the ticket. The `visitOn` helper is duplicated in two test classes (two occurrences, below the "third occurrence" threshold of the principles).
- F5 (no constraint, creation only) needs no test or code.

Questions for the developer or the business: none.

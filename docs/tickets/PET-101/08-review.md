# PET-101 · Review

## Rules, behaviour and tests
| Rule | Implemented as agreed | Test | Verdict |
|---|---|---|---|
| F1 same pet, same date: refused on the date field, nothing saved | yes, `VisitController.java:103-105`, `Pet.hasVisitOn` | `VisitControllerTests.f1_refusesSecondVisitOfSamePetOnSameDate`; `PetTests.f1_*` (4 tests) | covered |
| F2 exact English sentence, English only | yes, `duplicate.visitDate` in the 10 bundles | `VisitControllerTests.f2_duplicateVisitDateMessageHasAgreedTextInEveryBundle` (10 locales), `f2_localeBundleDefinesDuplicateVisitDateItself` (9 files), `f2_refusedDuplicateVisitRendersAgreedMessage`, `f2_refusedDuplicateVisitRendersEnglishMessageInOtherLanguage` | covered (was weak at first review; tests strengthened, gates re-run) |
| F3 same pet, other date: accepted | yes | `VisitControllerTests.f3_acceptsVisitOfSamePetOnDifferentDate`; `PetTests.f3_visitOnDifferentDateDoesNotCount` | covered |
| F4 other pet, same date: accepted | yes, only `owner.getPet(petId)` is checked | `VisitControllerTests.f4_acceptsVisitWhenOnlyAnotherPetHasVisitOnDate` | covered |
| F5 creation only, no DB constraint, existing duplicates untouched | yes, no schema or other path changed | none (scope rule, confirmed from the diff) | covered by diff review |
| F6 past date: only the future-date error | yes, `else if` at `VisitController.java:103` | `VisitControllerTests.f6_pastDuplicateDateShowsOnlyFutureDateError` | covered |

## Functional documentation updated
- `docs/functional/README.md` (new): index of functional areas.
- `docs/functional/visits.md` (new): rules in force for visits: description required, date in the future, and F1-F6 of PET-101. The project had no functional documentation before.

## Remarks from the team's past reviews
Skipped: the project has no pull request history (its `origin` is a local folder, no GitHub repository), so `docs/review-kb/` cannot be built.

## For the human reviewer
- Look first at `VisitController.processNewVisitForm` (3 lines) and `Pet.hasVisitOn`: it skips the visit being created by identity, because `loadPetWithVisit` attaches the new visit to the pet before validation.
- Choices: rule as a small query on `Pet` + check in the controller next to the existing future-date check; no database constraint (best effort against simultaneous bookings, developer's answer 3); English sentence copied into every locale bundle so that the existing `I18nPropertiesSyncTest` stays as is (answer 7). No ADR: the project has none and nothing here deserves one.
- Left out on purpose: database unique constraint, cleaning existing duplicates, translations, edit/cancel of visits (not existing).
- Notes from the reviews, not changed: `hasVisitOn` also guards against a null date although the controller already does; the new tests use `@DisplayName` and `fN_` names, which the rest of the suite does not; one display name lacks a space ("F3:a visit ...", `VisitControllerTests.java:232`).
- Quality: 109 tests green (2 Docker tests skipped), line coverage 93.8%, conventions review with no finding to fix.

## Pull request summary
**PET-101: refuse a second visit for the same pet on the same date**

A pet can no longer get two visits on the same calendar date. When the clinic books a visit on a date where the pet already has one, the form is shown again with "This pet already has a visit on this date." on the date field and nothing is saved. Other pets, and other dates for the same pet, are unaffected. A date in the past still shows only "Visit date must be in the future".

- `Pet.hasVisitOn(LocalDate, Visit ignored)`: new query method.
- `VisitController.processNewVisitForm`: duplicate check after the future-date check.
- `messages*.properties`: new key `duplicate.visitDate`, same English sentence in every bundle.
- Tests: `VisitControllerTests` (F1-F4, F6, F2 on every bundle and on the rendered page), new `PetTests`.
- No schema change; best effort against simultaneous bookings.

Ticket folder: `docs/tickets/PET-101/` (ticket, understanding, fast plan with the developer's answers, agent logs, quality and review reports); functional rules: `docs/functional/visits.md`.

🤖 Generated with [Claude Code](https://claude.com/claude-code)

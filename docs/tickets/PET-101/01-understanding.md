# PET-101 · Understanding

## What is asked
The clinic wants to stop staff from booking two visits for the same pet on the same calendar day. Today nothing prevents it, so it happens by mistake. When a second visit is entered for a pet that already has one on that date, it must be refused with a clear message, and the first visit must stay as it is.

## Where it lands in the code
Base path: `src/main/java/org/springframework/samples/petclinic/owner/`.
- `VisitController.java:97-112` (`processNewVisitForm`): the only place where a visit is created. It already rejects a date that is not in the future with `result.rejectValue("date", "typeMismatch.visitDate")` (lines 100-102) and re-renders the form on errors (104-106). The same-day check will most likely sit next to it, or in a model method it calls. The success flash message "Your visit has been booked" is hardcoded English (line 110).
- `VisitController.java:63-81` (`loadPetWithVisit`): runs before every GET and POST and already adds the new, unsaved visit to the pet (`pet.addVisit(visit)`, line 79). A check that walks `pet.getVisits()` must ignore that instance, or it will always find itself.
- `Visit.java:39-41`: `date` is a `LocalDate` (column `visit_date`), no time part. Default date is tomorrow (line 51). `Visit` has no `equals`/`hashCode`.
- `Pet.java:59` holds `visits` as a `LinkedHashSet<Visit>`; `addVisit` at line 81. A small query method such as "has a visit on date X" would fit here.
- `Owner.java:176-186` (`addVisit(petId, visit)`): asserts and delegates to `Pet.addVisit`; no business rule today.
- `src/main/resources/templates/pets/createOrUpdateVisitForm.html:32`: the date field uses the `inputField` fragment, which shows field errors, so an error attached to `date` appears with no template change.
- `src/main/resources/messages/messages.properties:9`: `typeMismatch.visitDate=Visit date must be in the future`. Ten locale bundles sit beside it (`de, en, es, fa, hi, ja, ko, pt, ru, tr`); a new message key goes into the default bundle and, depending on the answer below, the translated ones.
- Schema: `src/main/resources/db/h2/schema.sql:58-65` (and the `mysql` and `postgres` equivalents) has no uniqueness on `visits (pet_id, visit_date)`, only an index on `pet_id`.
- Tests: `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java:97-106` pins the future-date error code; its mocked pet has no visits, so a duplicate test needs a pet with a visit on the posted date.

## Existing rules and constraints
- ADRs: none (`docs/adr` does not exist).
- Project knowledge: none (`.claude/skills/project-knowledge` does not exist). `CLAUDE.md` holds build notes only.
- Business rule already coded: a new visit's date must be strictly after today (`VisitController.java:100`). So past visits can never collide with a new booking; only future visits matter.
- Schema precedent: pets already have a database-level uniqueness rule (`h2/schema.sql:55`, `UNIQUE (owner_id, name)`), so a uniqueness constraint on visits would follow an existing pattern.
- Sample data (`db/*/data.sql:50-53`): four visits, no same-day duplicates for a pet.
- Technical constraints:
  - Concurrency: a check-then-save in the controller is not atomic; a double click or two receptionists at once can still create a duplicate. Only a unique constraint on `(pet_id, visit_date)` guarantees it, and its violation would have to be mapped to the same message instead of an error page.
  - The project has no migration tool: schemas are plain `schema.sql` files per database (h2, mysql, postgres). A constraint would fail on a real database that already holds duplicates.
  - Performance: negligible; visits are loaded with the pet.
  - Security and personal data: not touched.

## Gaps and contradictions
1. "Same day" means the calendar date: the model has no time, so a morning and an evening visit on one day would both be refused. Confirm that is intended (no exception for emergencies, for example).
2. Message: exact wording, whether it names the date, and whether it must be translated into all the existing languages or added in English only.
3. Protection level: is a form check enough, or must the rule also hold against simultaneous bookings (database constraint)?
4. Existing duplicates in real databases: clean them, leave them, or not a concern? This decides whether a database constraint is possible.
5. Scope of the rule: only the "new visit" form exists today (no edit or cancel of a visit, no status field). Confirm nothing else is in scope.

## Size
small: one controller check (or a small `Pet` method), one message key and two or three tests; it becomes medium only if a database constraint across the three schemas is wanted.

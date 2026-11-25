# PET-101 · Retro

## Learned
- A new key in `messages.properties` must be added to every `messages_xx.properties` file (except `messages_en`), in English when there is no translation → `.claude/skills/project-knowledge/references/i18n.md`
- In the owner and pet controllers, the `@ModelAttribute` loader attaches the new object to its parent before validation, so a check over the parent's collection must leave out the object being created → `.claude/skills/project-knowledge/references/web.md`
- Edit the `messages*.properties` files byte-safely: CRLF line endings, some non-UTF-8 bytes (`messages_de.properties`) → `.claude/skills/project-knowledge/references/build.md`

## Changed or removed
None (the knowledge base was created by this retro).

## ADR candidates
None.

## Proposed and not kept
Kept here so the same proposal is not made again.
- "A form field shows at most one error: a business check on a field runs only when the field passed the earlier checks": dropped by the developer, "that was for this form, not a rule for every form."
- "Test names cite the rule with `@DisplayName("Fn: ...")`": a workflow rule, not a project one.
- "No database constraint for business uniqueness": the developer's answer 3 was specific to this ticket.

## Follow-up
- Developer's merge note: "fix the F3 typo in the display name next time, not now": `VisitControllerTests.java:232`, `"F3:a visit ..."` lacks a space.

---
name: project-knowledge
description: What this team has learned and decided about its own code base, one ticket at a time - where things live, the patterns to follow, the mistakes not to repeat. Use before reading, planning, writing or reviewing code in this repository.
---

# Project knowledge

This is the memory of the project. It is filled by the retro of each ticket (Spring agentic workflow, step 10) and validated by a developer. It outranks general principles; ADRs and the decisions of the current ticket outrank it.

Read this index, then open only the topic files that match what you are about to do.

## Topics
| Topic | File | Read it when |
|---|---|---|
| i18n | `references/i18n.md` | you add or change a message key in `messages*.properties` |
| Web layer | `references/web.md` | you add a check or validation in a controller that loads a parent with `@ModelAttribute` (owners, pets, visits) |
| Build and files | `references/build.md` | you edit resource files, in particular `messages*.properties`, from the shell or a tool |

## How an entry looks
```markdown
### <the rule, in one sentence, as an instruction>
- Why: <the reason, or what goes wrong otherwise>
- Example: `path/to/File.java:line`
- Source: <ticket>, <developer correction | review finding | decision | failed check>
```

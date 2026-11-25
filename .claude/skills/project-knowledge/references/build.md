# Build and files

### Edit the `messages*.properties` files byte-safely: they use CRLF line endings and some contain non-UTF-8 bytes (`messages_de.properties`)
- Why: on this Windows setup, `sed -i` in Git Bash stripped the CRLF endings and changed nothing else; an editor that re-encodes the file could also damage the German characters.
- Example: `src/main/resources/messages/messages_de.properties:7` ("ungültiges", not stored as UTF-8).
- Source: PET-101, failed edit (step 6), restored from git.

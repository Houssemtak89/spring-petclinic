# Web layer (controllers and forms)

### In the owner and pet controllers, the `@ModelAttribute` loader attaches the new object to its parent before validation, so a check over the parent's collection must leave out the object being created
- Why: otherwise the new visit or pet always finds itself, and every booking is refused.
- Example: `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java:79` (`pet.addVisit(visit)` in `loadPetWithVisit`); `Pet.hasVisitOn(date, ignored)` skips it by identity; same pattern with `owner.addPet(pet)` in `PetController.java:103`.
- Source: PET-101, analyst finding (step 1), applied in T1.

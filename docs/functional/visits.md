## Visits

### Booking a visit
- A visit is booked for one pet of an owner, with a date and a description.
- The description is required. A visit without a description is refused.
- The date of a visit must be in the future. Today and past dates are refused with the message "Visit date must be in the future".
- A pet cannot have two visits on the same calendar date. Booking a second visit for a date on which the pet already has a visit is refused: the visit form is shown again, the error appears on the date field, and nothing is saved. (PET-101, F1)
- The refusal message is exactly "This pet already has a visit on this date." It is the same sentence in every language of the application. (PET-101, F2)
- A visit for the same pet on a different date is accepted. (PET-101, F3)
- The rule is per pet: if only another pet of the same owner has a visit on that date, the booking is accepted. (PET-101, F4)
- When the date is not in the future, only "Visit date must be in the future" is shown; the same-date check applies only to a date that is in the future. (PET-101, F6)
- The same-date rule applies when a visit is created only. Duplicates that already exist are left as they are. Two bookings made at the very same moment may both go through: the check is made on the form, not guaranteed by the database. (PET-101, F5)

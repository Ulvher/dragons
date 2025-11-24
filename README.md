# SpaceX Dragon Rockets Repository (Library)

Simple library to manage rockets.

## Requirements Implemented

- Add new rocket (initial status: `ON_GROUND`).
- Change rocket status.
- Add new mission (initial status: `SCHEDULED`).
- Change mission status. (with recalculation)
- Automatically recalculate mission status when rocket status changed.
- Assign rocket to mission.

## Assumptions / Notes

1. **In-memory store only.** No persistence, no frameworks.
2. **Change status for rockets.** We can change status even if rocket is not assign to mission.
3. **Assign rocket to mission.** We change status of mission to `IN_PROGRESS` or to `PENDING` if racket is in `IN_REPAIR` status.
4. **Change mission status.** We allow manual override, but keep domain invariant by recalculate afterward

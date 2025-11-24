# SpaceX Dragon Rockets Repository (Library)

Simple library to manage rockets.

## Requirements Implemented

- Add new rocket (initial status: `ON_GROUND`).
- Change rocket status.

## Assumptions / Notes

1. **In-memory store only.** No persistence, no frameworks.
2. **Change status for rockets.** We can change status even if rocket is not assign to mission.

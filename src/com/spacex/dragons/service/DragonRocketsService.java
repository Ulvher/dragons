package com.spacex.dragons.service;

import com.spacex.dragons.MissionSummary;
import com.spacex.dragons.enums.MissionStatus;
import com.spacex.dragons.enums.RocketStatus;
import com.spacex.dragons.model.Mission;
import com.spacex.dragons.model.Rocket;

import java.util.*;
import java.util.stream.Collectors;

public class DragonRocketsService {
    private final List<Rocket> rockets = new ArrayList<>();
    private final List<Mission> missions = new ArrayList<>();

    // -------- Rockets --------
    public Rocket addRocket(String rocketName) {
        if (rockets.stream().map(Rocket::getName).toList().contains(rocketName)) {
            throw new IllegalArgumentException("Rocket already exists: " + rocketName);
        }
        Rocket rocket = new Rocket(rocketName);
        rockets.add(rocket);
        return rocket;
    }

    public Rocket getRocket(String rocketName) {
        Optional<Rocket> rocket = rockets.stream().filter(rocket1 -> Objects.equals(rocket1.getName(), rocketName)).findFirst();
        if (rocket.isEmpty())
            throw new NoSuchElementException("Rocket not found: " + rocketName);
        return rocket.get();
    }

    public void changeRocketStatus(String rocketName, RocketStatus newStatus) {
        Rocket rocket = getRocket(rocketName);
        rocket.setStatus(newStatus);

        if (rocket.hasAssignedMission()) {
            Mission mission = getMission(rocket.getAssignedMission());
            recalculateMissionStatus(mission);
        }
    }

    // -------- Missions --------
    public Mission addMission(String missionName) {
        if (missions.stream().map(Mission::getName).toList().contains(missionName)) {
            throw new IllegalArgumentException("Mission already exists: " + missionName);
        }
        Mission mission = new Mission(missionName);
        missions.add(mission);
        return mission;
    }

    public Mission getMission(String missionName) {
        Optional<Mission> mission = missions.stream().filter(mission1 -> Objects.equals(mission1.getName(), missionName)).findFirst();
        if (mission.isEmpty())
            throw new NoSuchElementException("Mission not found: " + missionName);
        return mission.get();
    }

    public void changeMissionStatus(String missionName, MissionStatus newStatus) {
        Mission mission = getMission(missionName);

        if (mission.getStatus() == MissionStatus.ENDED) {
            throw new IllegalStateException("Mission already ended: " + missionName);
        }

        if (newStatus != MissionStatus.ENDED) {
            // allow manual override, but keep domain invariant by recalculate afterward
            mission.setStatus(newStatus);
            recalculateMissionStatus(mission);
        } else {
            mission.setStatus(MissionStatus.ENDED);
        }
    }

    // -------- Assignment --------
    public void assignRocketToMission(String rocketName, String missionName) {
        Rocket rocket = getRocket(rocketName);
        Mission mission = getMission(missionName);

        if (mission.getStatus() == MissionStatus.ENDED) {
            throw new IllegalStateException("Cannot assign rockets to ended mission: " + missionName);
        }

        if (rocket.hasAssignedMission()) {
            throw new IllegalStateException("Rocket already assigned to a mission: " + rocketName);
        }

        rocket.assignToMission(mission.getName());
        mission.addRocket(rocket);
        recalculateMissionStatus(mission);
    }

    // -------- Summary --------

    public List<MissionSummary> getMissionsSummary() {
        missions.sort(
                Comparator
                        .comparingInt(Mission::rocketsCount).reversed()
                        .thenComparing(Mission::getName, Comparator.reverseOrder())
        );

        return missions.stream()
                .map(MissionSummary::new)
                .collect(Collectors.toList());
    }

    // -------- Internals --------
    private void recalculateMissionStatus(Mission mission) {
        if (mission.getStatus() == MissionStatus.ENDED)
            return;

        if (mission.getAssignedRockets().stream().anyMatch(r -> r.getStatus() == RocketStatus.IN_REPAIR)) {
            mission.setStatus(MissionStatus.PENDING);
            return;
        }

        if (!mission.getAssignedRockets().isEmpty()) {
            mission.setStatus(MissionStatus.IN_PROGRESS);
            return;
        }

        mission.setStatus(MissionStatus.SCHEDULED);
    }
}

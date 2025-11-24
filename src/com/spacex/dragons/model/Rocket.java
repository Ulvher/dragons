package com.spacex.dragons.model;

import com.spacex.dragons.enums.RocketStatus;

import java.util.Objects;

public class Rocket {
    private final String name;
    private RocketStatus status;
    private String assignedMission;

    public Rocket(String name) {
        this.name = name;
        this.status = RocketStatus.ON_GROUND;
        this.assignedMission = null;
    }

    public String getName() {
        return name;
    }

    public RocketStatus getStatus() {
        return status;
    }

    public String getAssignedMission() {
        return Objects.requireNonNullElse(assignedMission, "");
    }

    public Boolean hasAssignedMission() {
        return assignedMission != null && !assignedMission.equals("");
    }

    public void setStatus(RocketStatus status) {
        this.status = status;
    }

    public void assignToMission(String missionName) {
        this.assignedMission = missionName;
    }
}

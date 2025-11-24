package com.spacex.dragons.model;

import com.spacex.dragons.enums.RocketStatus;

public class Rocket {
    private String name;
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
        return assignedMission;
    }
}

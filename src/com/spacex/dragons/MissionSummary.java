package com.spacex.dragons;

import com.spacex.dragons.model.Mission;
import com.spacex.dragons.model.Rocket;

import java.util.List;

public class MissionSummary {
    private final Mission mission;
    private final List<Rocket> rockets;

    public MissionSummary(Mission mission) {
        this.mission = mission;
        this.rockets = List.copyOf(mission.getAssignedRockets());
    }

    public Mission getMission() {
        return mission;
    }

    public List<Rocket> getRockets() {
        return rockets;
    }
}

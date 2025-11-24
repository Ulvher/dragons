package com.spacex.dragons;

import com.spacex.dragons.model.Mission;

import java.util.ArrayList;
import java.util.List;

public class MissionSummary {
    private final Mission mission;

    public MissionSummary(Mission mission) {
        this.mission = mission;
    }

    public List<String> getMissionSummary() {
        List<String> result = new ArrayList<>();
        result.add("* " + mission.getName() + " - " + mission.getStatus() + " - " + "Dragons: " + mission.rocketsCount());
        mission.getAssignedRockets().forEach(rocket -> result.add("** " + rocket.getName() + " - " + rocket.getStatus()));
        return result;
    }
}

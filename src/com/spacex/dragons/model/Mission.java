package com.spacex.dragons.model;

import com.spacex.dragons.enums.MissionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Mission {
    private String name;
    private MissionStatus status;
    private List<Rocket> assignedRockets = new ArrayList<>();

    public Mission(String name) {
        this.name = name;
        this.status = MissionStatus.SCHEDULED;
    }

    public String getName() {
        return name;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public List<String> getAssignedRockets() {
        return assignedRockets.stream().map(Rocket::getName).collect(Collectors.toList());
    }

    public int rocketsCount() {
        return assignedRockets.size();
    }
}

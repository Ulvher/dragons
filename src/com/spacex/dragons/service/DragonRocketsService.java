package com.spacex.dragons.service;

import com.spacex.dragons.enums.RocketStatus;
import com.spacex.dragons.model.Rocket;

import java.util.*;

public class DragonRocketsService {
    private final List<Rocket> rockets = new ArrayList<>();

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
    }
}

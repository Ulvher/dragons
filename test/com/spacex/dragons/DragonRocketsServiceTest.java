package com.spacex.dragons;

import com.spacex.dragons.enums.RocketStatus;
import com.spacex.dragons.model.Rocket;
import com.spacex.dragons.service.DragonRocketsService;
import org.junit.jupiter.api.*;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class DragonRocketsServiceTest {
    private DragonRocketsService dragonRocketsService;

    @BeforeEach
    void setup() {
        dragonRocketsService = new DragonRocketsService();
    }

    @Test
    void addRocket_initialStatusOnGround_andUnassigned() {
        // Test
        Rocket r = dragonRocketsService.addRocket("Dragon 1");

        // Assert
        assertEquals(RocketStatus.ON_GROUND, r.getStatus());
        assertTrue(r.getAssignedMission().isEmpty());
    }

    @Test
    void getRocket_existingRocket() {
        // Setup
        String rocketName = "Dragon 1";
        dragonRocketsService.addRocket(rocketName);

        // Test
        Rocket r = dragonRocketsService.getRocket(rocketName);

        // Assert
        assertEquals(RocketStatus.ON_GROUND, r.getStatus());
        assertTrue(r.getAssignedMission().isEmpty());
        assertEquals(rocketName, r.getName());
    }

    @Test
    void getRocket_notExistingRocket() {
        // Setup
        dragonRocketsService.addRocket("Dragon 1");

        // Assert
        assertThrows(NoSuchElementException.class, () -> dragonRocketsService.getRocket("Dragon 2"));
    }

    @Test
    void changeRocketStatus() {
        // Setup
        Rocket r = dragonRocketsService.addRocket("Dragon 1");

        // Test
        dragonRocketsService.changeRocketStatus("Dragon 1", RocketStatus.IN_REPAIR);

        // Assert
        assertEquals(RocketStatus.IN_REPAIR, r.getStatus());
    }
}

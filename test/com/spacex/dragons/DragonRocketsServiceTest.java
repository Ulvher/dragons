package com.spacex.dragons;

import com.spacex.dragons.enums.MissionStatus;
import com.spacex.dragons.enums.RocketStatus;
import com.spacex.dragons.model.Mission;
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

    @Test
    void addMission_initialStatusScheduled() {
        // Test
        Mission m = dragonRocketsService.addMission("Mars");

        // Assert
        assertEquals(MissionStatus.SCHEDULED, m.getStatus());
        assertEquals(0, m.rocketsCount());
    }

    @Test
    void getMission_existingMission() {
        // Setup
        String missionName = "Mars";
        dragonRocketsService.addMission(missionName);

        // Test
        Mission mission = dragonRocketsService.getMission(missionName);

        // Assert
        assertEquals(MissionStatus.SCHEDULED, mission.getStatus());
        assertEquals(mission.rocketsCount(), 0);
        assertEquals("Mars", mission.getName());
    }

    @Test
    void assignRocketToMission_setsRocketMission_andMissionInProgress() {
        // Setup
        String missionName = "Luna1";
        String rocketName = "Dragon 1";
        dragonRocketsService.addRocket(rocketName);
        dragonRocketsService.addMission(missionName);

        // Test
        dragonRocketsService.assignRocketToMission(rocketName, missionName);
        Rocket r = dragonRocketsService.getRocket(rocketName);
        Mission m = dragonRocketsService.getMission(missionName);

        // Assert
        assertEquals(missionName, r.getAssignedMission());
        assertEquals(1, m.rocketsCount());
        assertEquals(MissionStatus.IN_PROGRESS, m.getStatus());
    }

    @Test
    void rocketCanOnlyBeAssignedToOneMission() {
        // Setup
        dragonRocketsService.addRocket("Dragon 1");
        dragonRocketsService.addMission("Luna1");
        dragonRocketsService.addMission("Mars");
        dragonRocketsService.assignRocketToMission("Dragon 1", "Luna1");

        // Assert
        assertThrows(IllegalStateException.class, () -> dragonRocketsService.assignRocketToMission("Dragon 1", "Mars"));
    }

    @Test
    void cannotAssignToEndedMission() {
        // Setup
        dragonRocketsService.addRocket("Dragon 1");
        dragonRocketsService.addMission("Luna1");
        dragonRocketsService.changeMissionStatus("Luna1", MissionStatus.ENDED);

        // Assert
        assertThrows(IllegalStateException.class, () -> dragonRocketsService.assignRocketToMission("Dragon 1", "Luna1"));
    }

    @Test
    void rocketInRepair_makesMissionPending() {
        // Setup
        dragonRocketsService.addRocket("Dragon 1");
        dragonRocketsService.addRocket("Dragon 2");
        dragonRocketsService.addMission("Luna1");
        dragonRocketsService.assignRocketToMission("Dragon 1", "Luna1");
        dragonRocketsService.assignRocketToMission("Dragon 2", "Luna1");

        // Test
        dragonRocketsService.changeRocketStatus("Dragon 2", RocketStatus.IN_REPAIR);

        // Assert
        assertEquals(MissionStatus.PENDING, dragonRocketsService.getMission("Luna1").getStatus());
    }

    @Test
    void leavingRepair_recalculateMissionStatusToInProgress() {
        // Setup
        dragonRocketsService.addRocket("Dragon 1");
        dragonRocketsService.addMission("Luna1");
        dragonRocketsService.assignRocketToMission("Dragon 1", "Luna1");
        dragonRocketsService.changeRocketStatus("Dragon 1", RocketStatus.IN_REPAIR);
        assertEquals(MissionStatus.PENDING, dragonRocketsService.getMission("Luna1").getStatus());

        // Test
        dragonRocketsService.changeRocketStatus("Dragon 1", RocketStatus.IN_SPACE);

        // Assert
        assertEquals(MissionStatus.IN_PROGRESS, dragonRocketsService.getMission("Luna1").getStatus());
    }
}

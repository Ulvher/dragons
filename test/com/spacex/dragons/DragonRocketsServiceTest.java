package com.spacex.dragons;

import com.spacex.dragons.enums.MissionStatus;
import com.spacex.dragons.enums.RocketStatus;
import com.spacex.dragons.model.Mission;
import com.spacex.dragons.model.Rocket;
import com.spacex.dragons.service.DragonRocketsService;
import org.junit.jupiter.api.*;

import java.util.List;
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

    @Test
    void summaryOrdersByRocketCountDescAndNameDesc() {
        // Setup
        dragonRocketsService.addMission("Mars");
        dragonRocketsService.addMission("Luna1");
        dragonRocketsService.addMission("Double Landing");
        dragonRocketsService.addMission("Transit");
        dragonRocketsService.addMission("Luna2");
        dragonRocketsService.addMission("Vertical Landing");

        dragonRocketsService.addRocket("Dragon 1");
        dragonRocketsService.addRocket("Dragon 2");
        dragonRocketsService.addRocket("Red Dragon");
        dragonRocketsService.addRocket("Dragon XL");
        dragonRocketsService.addRocket("Falcon Heavy");

        dragonRocketsService.assignRocketToMission("Dragon 1", "Luna1");
        dragonRocketsService.assignRocketToMission("Dragon 2", "Luna1");
        dragonRocketsService.changeMissionStatus("Double Landing", MissionStatus.ENDED);
        dragonRocketsService.assignRocketToMission("Red Dragon", "Transit");
        dragonRocketsService.assignRocketToMission("Dragon XL", "Transit");
        dragonRocketsService.assignRocketToMission("Falcon Heavy", "Transit");
        dragonRocketsService.changeMissionStatus("Vertical Landing", MissionStatus.ENDED);

        // make Luna1 pending by putting one rocket in repair
        dragonRocketsService.changeRocketStatus("Dragon 2", RocketStatus.IN_REPAIR);

        // Test
        List<String> missionsInOrder = dragonRocketsService.getMissionsSummary().stream().flatMap(item -> item.getMissionSummary().stream()).toList();

        // Assert
        assertEquals(List.of(
                "* Transit - IN_PROGRESS - Dragons: 3",
                "** Red Dragon - ON_GROUND",
                "** Dragon XL - ON_GROUND",
                "** Falcon Heavy - ON_GROUND",
                "* Luna1 - PENDING - Dragons: 2",
                "** Dragon 1 - ON_GROUND",
                "** Dragon 2 - IN_REPAIR",
                "* Vertical Landing - ENDED - Dragons: 0",
                "* Mars - SCHEDULED - Dragons: 0",
                "* Luna2 - SCHEDULED - Dragons: 0",
                "* Double Landing - ENDED - Dragons: 0"
                        ), missionsInOrder);
    }

}

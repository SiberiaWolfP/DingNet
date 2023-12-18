package webserver.service;

import gui.MainGUI;
import iot.SimulationRunner;
import org.springframework.stereotype.Service;
import util.DingNetCache;
import util.MutableInteger;
import webserver.DTO.WorldOptionsDTO;

import java.io.File;

@Service
public class WorldService {

    public boolean initWorld(WorldOptionsDTO worldOptionsDTO) {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MainGUI mainGUI = MainGUI.getInstance();

        simulationRunner.setSimulationStatus(SimulationRunner.SimulationStatus.STOPPED);


        String projectPath = System.getProperty("user.dir");
        String configPath = projectPath + "/settings/configurations/";

        File configFile = new File(configPath + worldOptionsDTO.getConfigName());
        if (!configFile.exists()) {
            return false;
        }
        simulationRunner.loadConfigurationFromFile(configFile);

        simulationRunner.getSimulation().setInputProfile(simulationRunner.getInputProfiles().get(0));
        simulationRunner.updateQoS(simulationRunner.getInputProfiles().get(0).getQualityOfServiceProfile());

        simulationRunner.setSpeed(new MutableInteger(worldOptionsDTO.getSpeed()));

//        if (worldOptionsDTO.getConfigName().equals("mock.xml")) {
//            simulationRunner.resetMap();
//            simulationRunner.resetGateways(5);
//            simulationRunner.resetMote();
//        }

        // Disable DingNet built-in adaptation
        simulationRunner.setApproach("No Adaptation");
        mainGUI.updateEntries(simulationRunner.getEnvironment());
        mainGUI.loadMap(false);
        mainGUI.configureButton.setEnabled(true);

        return true;
    }

    public void start() throws InterruptedException {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MainGUI mainGUI = MainGUI.getInstance();

        simulationRunner.setSimulationStatus(SimulationRunner.SimulationStatus.STOPPED);

        simulationRunner.getSimulation().setInputProfile(simulationRunner.getInputProfiles().get(0));
        simulationRunner.updateQoS(simulationRunner.getInputProfiles().get(0).getQualityOfServiceProfile());
        simulationRunner.setupSingleRun();
        simulationRunner.reconnect();
        simulationRunner.simulate(mainGUI);
    }

    public void resetEntities() {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MainGUI mainGUI = MainGUI.getInstance();

        simulationRunner.setSimulationStatus(SimulationRunner.SimulationStatus.STOPPED);

        simulationRunner.resetGateways(5);
        simulationRunner.resetMote();
        simulationRunner.setupSingleRun();

        mainGUI.updateEntries(simulationRunner.getEnvironment());
        mainGUI.loadMap(false);
    }

    public void resetGateways() {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MainGUI mainGUI = MainGUI.getInstance();

        simulationRunner.setSimulationStatus(SimulationRunner.SimulationStatus.STOPPED);

        simulationRunner.resetGateways(5);
        simulationRunner.setupSingleRun();

        mainGUI.updateEntries(simulationRunner.getEnvironment());
        mainGUI.loadMap(false);
    }

    public void resetMote() {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MainGUI mainGUI = MainGUI.getInstance();

        simulationRunner.setSimulationStatus(SimulationRunner.SimulationStatus.STOPPED);

        simulationRunner.resetMote();
        simulationRunner.setupSingleRun();

        mainGUI.updateEntries(simulationRunner.getEnvironment());
        mainGUI.loadMap(false);
    }

    public void resetMap() {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MainGUI mainGUI = MainGUI.getInstance();

        simulationRunner.setSimulationStatus(SimulationRunner.SimulationStatus.STOPPED);

        simulationRunner.resetMap();
        simulationRunner.setupSingleRun();

        mainGUI.updateEntries(simulationRunner.getEnvironment());
        mainGUI.loadMap(false);
    }
}

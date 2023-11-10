package webserver.service;

import iot.SimulationRunner;
import org.springframework.stereotype.Service;
import webserver.DTO.MonitorDTO;

import java.util.HashMap;

@Service
public class MonitorService {

    public MonitorDTO collectMonitorData() {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MonitorDTO monitor = new MonitorDTO();
        monitor.setMotes(simulationRunner.getEnvironment().getMotes());
        monitor.setGateways(simulationRunner.getEnvironment().getGateways());
        monitor.setAdaptationGoals(new HashMap<>());
        simulationRunner.getInputProfiles().forEach(inputProfile -> {
            String QoSName = inputProfile.getQualityOfServiceProfile().getNames().iterator().next();
            monitor.getAdaptationGoals().put(inputProfile.getName(), inputProfile.getQualityOfServiceProfile().getAdaptationGoal(QoSName));
        });
        return monitor;
    }
}

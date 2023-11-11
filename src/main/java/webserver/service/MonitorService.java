package webserver.service;

import iot.SimulationRunner;
import org.springframework.stereotype.Service;
import selfadaptation.adaptationgoals.AdaptationGoal;
import webserver.DTO.AlgorithmDTO;
import webserver.DTO.MonitorDTO;
import webserver.DTO.QoSOptionsDTO;

import java.util.ArrayList;

@Service
public class MonitorService {

    public MonitorDTO collectMonitorData() {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MonitorDTO monitor = new MonitorDTO();

        monitor.setStatus(simulationRunner.getSimulationStatus());
        monitor.setMotes(simulationRunner.getEnvironment().getMotes());
        monitor.setGateways(simulationRunner.getEnvironment().getGateways());
        monitor.setAdaptationGoals(new ArrayList<>());
        simulationRunner.getQoS().getNames().forEach(name -> {
            QoSOptionsDTO QoS = new QoSOptionsDTO();
            QoS.setName(name);
            QoS.setAdaptationGoal((AdaptationGoal) simulationRunner.getQoS().getAdaptationGoal(name));
            monitor.getAdaptationGoals().add(QoS);
        });
        monitor.setAdaptationApproaches(new ArrayList<>());
        simulationRunner.getAlgorithms().forEach(algorithm -> {
            AlgorithmDTO algorithmDTO = new AlgorithmDTO();
            algorithmDTO.setName(algorithm.getName());
            algorithmDTO.setActive(algorithm.isActive());
            monitor.getAdaptationApproaches().add(algorithmDTO);
        });
        return monitor;
    }
}

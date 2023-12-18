package webserver.service;

import iot.Characteristic;
import iot.SimulationRunner;
import org.jxmapviewer.viewer.GeoPosition;
import org.springframework.stereotype.Service;
import selfadaptation.adaptationgoals.AdaptationGoal;
import util.MapHelper;
import webserver.DTO.AlgorithmDTO;
import webserver.DTO.MonitorDTO;
import webserver.DTO.MoteDTO;
import webserver.DTO.QoSOptionsDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonitorService {

    public MonitorDTO collectMonitorData() {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        MonitorDTO monitor = new MonitorDTO();

        monitor.setStatus(simulationRunner.getSimulationStatus());

        List<MoteDTO> moteDTOList = new ArrayList<>();
        simulationRunner.getEnvironment().getMotes().forEach(mote -> {
            MoteDTO moteDTO = new MoteDTO();
            moteDTO.setEUI(mote.getEUI());
            moteDTO.setLatitude(mote.getLatPos());
            moteDTO.setLongitude(mote.getLongPos());
            moteDTO.setTransmissionPower(mote.getTransmissionPower());
            moteDTO.setMovementSpeed(mote.getMovementSpeed());
            moteDTO.setBestSignalStrength(mote.getHighestSignalPower());
            moteDTO.setBestGatewayDistance(mote.getBestGatewayDistance());
            simulationRunner.getEnvironment().getGateways().forEach(gateway -> {
                if (gateway.getEUI() == mote.getBestGatewayEUI()) {
                    moteDTO.setBestGatewayLatitude(gateway.getLatPos());
                    moteDTO.setBestGatewayLongitude(gateway.getLongPos());
                }
            });
            int posX = (int)simulationRunner.getEnvironment().getMapHelper().toMapXCoordinate(mote.getPos());
            int posY = (int)simulationRunner.getEnvironment().getMapHelper().toMapYCoordinate(mote.getPos());
            Characteristic characteristic = simulationRunner.getEnvironment().getCharacteristic(posX, posY);
            moteDTO.setPathLoss(characteristic.getPathLossExponent());
            moteDTO.setShadowFading(characteristic.getShadowFading());
            moteDTOList.add(moteDTO);
        });
        monitor.setMotes(moteDTOList);

        GeoPosition minMapPosition = simulationRunner.getEnvironment().getMapHelper().toGeoPosition(0, 0);
        GeoPosition maxMapPosition = simulationRunner.getEnvironment().getMapHelper().
            toGeoPosition(simulationRunner.getEnvironment().getMaxXpos(), simulationRunner.getEnvironment().getMaxYpos());
        monitor.setMinMapPosition(minMapPosition);
        monitor.setMaxMapPosition(maxMapPosition);
        monitor.setMaxDistance(MapHelper.distance(minMapPosition, maxMapPosition) * 1000);

//        monitor.setGateways(simulationRunner.getEnvironment().getGateways());
//        monitor.setAdaptationGoals(new ArrayList<>());
//        simulationRunner.getQoS().getNames().forEach(name -> {
//            QoSOptionsDTO QoS = new QoSOptionsDTO();
//            QoS.setName(name);
//            QoS.setAdaptationGoal((AdaptationGoal) simulationRunner.getQoS().getAdaptationGoal(name));
//            monitor.getAdaptationGoals().add(QoS);
//        });
//        monitor.setAdaptationApproaches(new ArrayList<>());
//        simulationRunner.getAlgorithms().forEach(algorithm -> {
//            AlgorithmDTO algorithmDTO = new AlgorithmDTO();
//            algorithmDTO.setName(algorithm.getName());
//            algorithmDTO.setActive(algorithm.isActive());
//            monitor.getAdaptationApproaches().add(algorithmDTO);
//        });
        return monitor;
    }
}

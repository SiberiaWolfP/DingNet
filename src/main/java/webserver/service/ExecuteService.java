package webserver.service;

import iot.QualityOfService;
import iot.SimulationRunner;
import iot.networkentity.Mote;
import org.springframework.stereotype.Service;
import selfadaptation.adaptationgoals.AdaptationGoal;
import selfadaptation.adaptationgoals.IntervalAdaptationGoal;
import selfadaptation.adaptationgoals.ThresholdAdaptationGoal;
import webserver.DTO.ExecuteDTO;
import webserver.DTO.MoteOptionsDTO;
import webserver.DTO.QoSOptionsDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ExecuteService {

    public boolean executeAdaptation(ExecuteDTO executeDTO) {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();

//        if (checkAdaptationApproaches(executeDTO.getAdaptationApproaches())) {
//            simulationRunner.setApproach(executeDTO.getAdaptationApproaches());
//        } else {
//            return false;
//        }

//        if (checkAdaptationGoal(executeDTO.getAdaptationGoals())) {
//            QualityOfService qualityOfService = new QualityOfService(new HashMap<>());
//            qualityOfService.putAdaptationGoal(executeDTO.getAdaptationGoals().getName(),
//                (IntervalAdaptationGoal) executeDTO.getAdaptationGoals().getAdaptationGoal());
//            simulationRunner.updateQoS(qualityOfService);
//        } else {
//            return false;
//        }

        for (MoteOptionsDTO moteOptions : executeDTO.getMoteOptions()) {
            if (checkMoteOptions(moteOptions)) {
                Mote mathedMote = simulationRunner.getEnvironment().getMotes().stream()
                    .filter(mote -> mote.getEUI() == moteOptions.getEUI())
                    .findFirst()
                    .orElse(null);
                if (mathedMote == null) {
                    return false;
                }
                simulationRunner.getSimulation().getApproach().getMoteEffector().setPower(mathedMote, (int)moteOptions.getTransmissionPower());
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean checkAdaptationApproaches(String adaptationApproaches) {
        List<String> adaptationApproachesList = new ArrayList<>(){{
            add("No Adaptation");
            add("Signal-based");
            add("Distance-based");
        }};
        return adaptationApproachesList.contains(adaptationApproaches);
    }

    private boolean checkAdaptationGoal(QoSOptionsDTO adaptationGoal) {
        List<String> QoSName = new ArrayList<>(){{
            add("reliableCommunication");
        }};
        if (!QoSName.contains(adaptationGoal.getName())) {
            return false;
        }
        AdaptationGoal goal = adaptationGoal.getAdaptationGoal();
        if (goal instanceof IntervalAdaptationGoal) {
            IntervalAdaptationGoal intervalAdaptationGoal = (IntervalAdaptationGoal) adaptationGoal.getAdaptationGoal();
            if (intervalAdaptationGoal.getLowerBoundary() < -100.0 || intervalAdaptationGoal.getLowerBoundary() > 20.0) {
                return false;
            }
            if (intervalAdaptationGoal.getUpperBoundary() < -100.0 || intervalAdaptationGoal.getUpperBoundary() > 20.0) {
                return false;
            }
            if (intervalAdaptationGoal.getLowerBoundary() > intervalAdaptationGoal.getUpperBoundary()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkMoteOptions(MoteOptionsDTO moteOptions) {
        SimulationRunner simulationRunner = SimulationRunner.getInstance();
        AtomicBoolean moteExists = new AtomicBoolean(false);
        simulationRunner.getEnvironment().getMotes().forEach(mote -> {
            if (mote.getEUI() == moteOptions.getEUI()) {
                moteExists.set(true);
            }
        });
        if (!moteExists.get()) {
            return false;
        }
//        List<Integer> transmissionPowerOptions = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
//        if (!transmissionPowerOptions.contains(moteOptions.getTransmissionPower())) {
//            return false;
//        }
        return true;
    }
}

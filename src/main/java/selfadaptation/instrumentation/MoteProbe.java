package selfadaptation.instrumentation;

import iot.SimulationRunner;
import iot.lora.LoraTransmission;
import iot.networkentity.Gateway;
import iot.networkentity.Mote;
import selfadaptation.feedbackloop.GenericFeedbackLoop;
import util.MapHelper;

import java.util.*;

/**
 * A class representing methods for probing.
 */
public class MoteProbe {
    /**
     * A list with feedBackLoops using the probe.
     */
    private GenericFeedbackLoop genericFeedbackLoop;

    /**
     * A map to keep track of which gateway has already sent the packet.
     */
    private FeedbackLoopGatewayBuffer gatewayBuffer;

    /**
     * A HashMap representing the buffers for the approach.
     */
    private Map<Mote, List<Double>> reliableDistanceGatewayBuffers;

    /**
     * A HashMap representing the buffers for the approach.
     */

    private Map<Mote, List<Double>> reliableMinPowerBuffers;

    /**
     * Constructs a MoteProbe with no FeedBackLoops using it.
     */
    public MoteProbe() {
        gatewayBuffer = new FeedbackLoopGatewayBuffer();
        reliableDistanceGatewayBuffers = new HashMap<>();
        reliableMinPowerBuffers = new HashMap<>();
    }

    /**
     * Returns a list of GenericFeedbackLoops using the probe.
     * @return  A list of GenericFeedbackLoops using the probe.
     */
    public GenericFeedbackLoop getGenericFeedbackLoop() {
        return genericFeedbackLoop;
    }

    /**
     * Sets a GenericFeedbackLoop using the probe.
     * @param genericFeedbackLoop The FeedbackLoop to set.
     */
    public void setGenericFeedbackLoop(GenericFeedbackLoop genericFeedbackLoop) {
        this.genericFeedbackLoop = genericFeedbackLoop;
    }

    /**
     * Returns the spreading factor of a given mote.
     * @param mote The mote to generate the graph of.
     * @return the spreading factor of the mote
     */
    public int getSpreadingFactor(Mote mote) {
        return mote.getSF();
    }

    /**
     * Triggers the feedback loop.
     * @param gateway
     * @param devEUI
     */
    public void trigger(Gateway gateway, long devEUI) {
        SimulationRunner.getInstance().getEnvironment().getMotes().stream()
            .filter(m -> m.getEUI() == devEUI && getGenericFeedbackLoop().isActive())
            .reduce((a, b) -> b)
            .ifPresent(m -> {
                if (monitorMoteMetrics(m, gateway)) {
                    getGenericFeedbackLoop().adapt(m, gateway);
                }
            });
    }

    public int getPowerSetting(Mote mote) {
        return mote.getTransmissionPower();
    }

    public boolean monitorMoteMetrics(Mote mote, Gateway gateway) {
        boolean available = false;
        gatewayBuffer.add(mote, gateway);
        /**
         First we check if we have received the message already from all gateways.
         */
        if (gatewayBuffer.hasReceivedAllSignals(mote)) {
            /**
             * Check for the signal which has travelled the shortest distance.
             */
            List<LoraTransmission> receivedSignals = gatewayBuffer.getReceivedSignals(mote);
            var env = SimulationRunner.getInstance().getEnvironment();
            double shortestDistance = MapHelper.distance(env.getNetworkEntityById(receivedSignals.get(0).getReceiver()).getPos(), receivedSignals.get(0).getPos());

            for (LoraTransmission transmission : receivedSignals) {
                if (shortestDistance > MapHelper.distance(env.getNetworkEntityById(transmission.getReceiver()).getPos(), transmission.getPos())) {
                    shortestDistance = MapHelper.distance(env.getNetworkEntityById(transmission.getReceiver()).getPos(), transmission.getPos());
                }
            }

            /**
             * If the buffer has an entry for the current mote, the new distance to the nearest gateway is added to it,
             * else a new buffer is created and added to which we can add the distance to the nearest gateway.
             */
            List<Double> reliableDistanceGatewayBuffer;
            if (!reliableDistanceGatewayBuffers.containsKey(mote)) {
                reliableDistanceGatewayBuffers.put(mote, new LinkedList<>());
            }
            reliableDistanceGatewayBuffer = reliableDistanceGatewayBuffers.get(mote);
            reliableDistanceGatewayBuffer.add(shortestDistance);
            reliableDistanceGatewayBuffers.put(mote, reliableDistanceGatewayBuffer);
            /**
             * If the buffer for the mote has 4 entries, the algorithm can start making adjustments.
             */
            if (reliableDistanceGatewayBuffers.get(mote).size() == 4) {
                /**
                 * The average is taken of the 4 entries.
                 */
                double average = 0;
                for (double distance : reliableDistanceGatewayBuffers.get(mote)) {
                    average += distance;
                }
                average = average / 4 * 1000;

                this.genericFeedbackLoop.getMoteEffector().setDistance(mote, average);
                reliableDistanceGatewayBuffers.put(mote, new LinkedList<>());
                available = true;
            }

            double receivedPower = receivedSignals.get(0).getTransmissionPower();

            for (LoraTransmission transmission : receivedSignals) {
                if (receivedPower < transmission.getTransmissionPower()) {
                    receivedPower = transmission.getTransmissionPower();
                }
            }

            /**
             * If the buffer has an entry for the current mote, the new highest received signal strength is added to it,
             * else a new buffer is created and added to which we can add the signal strength.
             */
            List<Double> reliableMinPowerBuffer = new LinkedList<>();
            if (reliableMinPowerBuffers.containsKey(mote)) {
                reliableMinPowerBuffer = reliableMinPowerBuffers.get(mote);
            }
            reliableMinPowerBuffer.add(receivedPower);
            reliableMinPowerBuffers.put(mote, reliableMinPowerBuffer);
            /**
             * If the buffer for the mote has 5 entries, the algorithm can start making adjustments.
             */
            if (reliableMinPowerBuffers.get(mote).size() == 4) {
                /**
                 * The average is taken of the 5 entries.
                 */
                double average = reliableMinPowerBuffers.get(mote).stream()
                    .mapToDouble(o -> o)
                    .average()
                    .orElse(0L);

                this.genericFeedbackLoop.getMoteEffector().setSignalPower(mote, average);
                reliableMinPowerBuffers.put(mote, new LinkedList<>());
                available = true;
            }
        }
        return available;
    }

    public Optional<Double> getShortestDistance(Mote mote) {
        if (mote.getShortestDistanceToGateway() == Double.MAX_VALUE) {
            return Optional.empty();
        }
        else {
            return Optional.of(mote.getShortestDistanceToGateway());
        }
    }

    public Optional<Double> getHighestPower(Mote mote) {
        if (mote.getHighestSignalPower() == Double.MIN_VALUE) {
            return Optional.empty();
        }
        else {
            return Optional.of(mote.getHighestSignalPower());
        }
    }

}

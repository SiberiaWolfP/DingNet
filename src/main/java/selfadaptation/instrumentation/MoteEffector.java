package selfadaptation.instrumentation;

import iot.networkentity.Mote;

/**
 * A class to allow self-adaptation software to edit mote settings.
 */
public class MoteEffector {

    /**
     * Constructs a MoteEffector.
     */
    public MoteEffector() {
    }

    /**
     * A method to set the power of a mote.
     * @param mote The mote to set the power of.
     * @param power The power to set.
     */
    public void setPower(Mote mote, int power) {
        mote.setTransmissionPower(power);
    }

    /**
     * A method to set the spreading factor of a mote.
     * @param mote The mote to set the spreading factor of.
     * @param spreadingFactor The spreading factor to set.
     */
    public void setSpreadingFactor(Mote mote, int spreadingFactor) {
        mote.setSF(spreadingFactor);
    }

    /**
     * A method to set the movement speed of a mote.
     * @param mote The mote to set the movement speed of.
     * @param movementSpeed The movement speed to set.
     */
    public void setMovementSpeed(Mote mote, double movementSpeed) {
        mote.setMovementSpeed(movementSpeed);
    }

    /**
     * A method to set the energy level of a mote.
     * @param mote The mote to set the energy level of.
     * @param energyLevel The energy level to set.
     */
    public void setEnergyLevel(Mote mote, int energyLevel) {
        mote.setEnergyLevel(energyLevel);
    }

    /**
     * Attention: this method only used after the distance was calculated automatically.
     * @param mote The mote to set the distance of.
     * @param distance The distance to set.
     */
    public void setDistance(Mote mote, double distance) {
        mote.setShortestDistanceToGateway(distance);
    }

    public void setBestGatewayEUI(Mote mote, long gatewayEUI) {
        mote.setBestGatewayEUI(gatewayEUI);
    }

    public void setSignalPower(Mote mote, double power) {
        mote.setHighestSignalPower(power);
    }

    public void setBestGatewayDistance(Mote mote, double distance) {
        mote.setBestGatewayDistance(distance);
    }

}

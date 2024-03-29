package iot.lora;


import org.jxmapviewer.viewer.GeoPosition;
import util.Pair;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A class representing a packet in the LoraWan simulation.
 */
public class LoraTransmission implements Serializable {

    //region field
    private static final long serialVersionUID = 1L;

    /**
     * A network entity representing the sender of the packet.
     */
    private final long sender;

    /**
     * A network entity representing the receiver of the packet.
     */
    private final long receiver;

    /**
     * A double representing the transmission power.
     */
    private double transmissionPower = 0.0;

    /**
     * A GeoPosition representing the location of the packet.
     */
    private GeoPosition pos;



    /**
     * The content of the message.
     */
    private final LoraWanPacket content;

    /**
     * Set of parameter used by the {@link iot.networkentity.NetworkEntity} to send this transmission
     */
    private final RegionalParameter regionalParameter;

    /**
     * The departure time of the message
     */
    private final LocalDateTime departureTime;

    /**
     * The time on air of a transmission.
     */
    private final double timeOnAir;

    /**
     * true if the transmission is arrived to destination, false otherwise
     */
    private boolean arrived;


    /**
     * true if the transmission is collided to another one, false otherwise
     */
    private boolean collided;
    //endregion

    //region constructor
    /**
     * A constructor generating a transmission with a given sender, receiver, transmission power, bandwidth, spreading factor,
     * environment and content.
     * @param sender    The sender sending the transmission.
     * @param receiver The receiver receiving the transmission.
     * @param positionSender
     * @param transmissionPower The transmission power of the transmission.
     * @param content The content of the transmission.
     */
    public LoraTransmission(long sender, long receiver, GeoPosition positionSender,
                            double transmissionPower, RegionalParameter regionalParameter, double timeOnAir,
                            LocalDateTime departureTime, LoraWanPacket content) {

        this.sender = sender;
        this.receiver = receiver;
        this.pos = positionSender;
        this.content = content;
        this.arrived = false;
        this.collided = false;

        if (isValidTransmissionPower(transmissionPower)) {
            this.transmissionPower = transmissionPower;

        }

        this.regionalParameter = regionalParameter;
        this.departureTime = departureTime;
        this.timeOnAir = timeOnAir;
    }
    //endregion

    //region setter and getter
    /**
     *  Returns the sender of this transmission.
     * @return The sender of this transmission.
     */
    public long getSender() {
        return sender;
    }

    /**
     *  Returns the receiver of this transmission.
     * @return The receiver of this transmission.
     */
    public long getReceiver() {
        return receiver;
    }

    /**
     * Returns the departure time of the transmission.
     * @return  The departure time of the transmission.
     */
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    /**
     * Returns the time on air.
     * @return  The time on air.
     */
    public double getTimeOnAir() {
        return timeOnAir;
    }

    /**
     * Checks if a given transmission power is valid.
     * @param transmissionPower The transmission power to check.
     * @return  True if the transmission power is valid.
     */
    private static boolean isValidTransmissionPower(double transmissionPower) {
        return true;
    }

    /**
     *  Returns The transmission power of this transmission.
     * @return The transmission power of this transmission.
     */
    public double getTransmissionPower() {
        return transmissionPower;
    }

    /**
     * Sets the transmission power of the transmission
     * @param transmissionPower The transmission power to set.
     * @Post Sets the transmission power to the given value if the given value is valid.
     * @Post Sets the transmission power to 0 if the given value is not valid.
     */
    private void setTransmissionPower(double transmissionPower) {
        if (isValidTransmissionPower(transmissionPower)) {
            this.transmissionPower = transmissionPower;
        } else {
            this.transmissionPower = 0.0;
        }
    }

    /**
     * Returns the bandwidth of the transmission.
     * @return  The bandwidth of the transmission.
     */
    public int getBandwidth() {
        return regionalParameter.getBandwidth();
    }

    /**
     * Returns the spreading factor.
     * @return The spreading factor.
     */
    public int getSpreadingFactor() {
        return regionalParameter.getSpreadingFactor();
    }

    /**
     *
     * @return true if the transmission is arrived to destination, false otherwise
     */
    public boolean isArrived() {
        return arrived;
    }

    /**
     * set the transmission as arrived to destination
     * @return this
     */
    public LoraTransmission setArrived() {
        if (isArrived()) {
            throw new IllegalStateException("the transmission is already arrived, you can't modify again this property");
        }
        this.arrived = true;
        return this;
    }

    /**
     *
     * @return true if the transmission is collided with another one, false otherwise
     */
    public boolean isCollided() {
        return collided;
    }

    /**
     * set the transmission as collided with another one
     * @return this
     */
    public LoraTransmission setCollided() {
        if (isArrived()) {
            new IllegalStateException("the transmission is already arrived, you can't modify this property").printStackTrace();
        }
        if (!isCollided()) {
            this.collided = true;

        }
        return this;
    }

    /**
     * Returns the coordinates of the transmission.
     * @return The coordinates of the transmission.
     */
    public GeoPosition getPos() {
        return pos;
    }


    /**
     * Returns the content of the transmission.
     * @return  the content of the transmission.
     */
    public LoraWanPacket getContent() {
        return content;
    }

    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoraTransmission that = (LoraTransmission) o;
        return getSender() == that.getSender() &&
            getContent().equals(that.getContent()) &&
            getDepartureTime().equals(that.getDepartureTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender(), getReceiver(), getContent(), getDepartureTime());
    }
}

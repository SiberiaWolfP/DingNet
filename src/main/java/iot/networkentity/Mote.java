package iot.networkentity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import iot.Environment;
import iot.GlobalClock;
import iot.SimulationRunner;
import iot.lora.*;
import iot.strategy.consume.ConsumePacketStrategy;
import iot.strategy.store.MaintainLastPacket;
import iot.strategy.store.ReceivedPacketStrategy;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jxmapviewer.viewer.GeoPosition;
import selfadaptation.instrumentation.FeedbackLoopGatewayBuffer;
import util.MapHelper;
import util.Path;

import java.util.*;


/**
 * A class representing the energy bound and moving motes in the network.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class Mote extends NetworkEntity {

    //region field

    // Distance in km
    @Schema(hidden = true)
    @JsonIgnore
    public static final double DISTANCE_THRESHOLD_ROUNDING_ERROR = 0.001;

    //both in seconds
    @Schema(hidden = true)
    @JsonIgnore
    private static final int DEFAULT_START_SENDING_OFFSET = 1;

    @Schema(hidden = true)
    @JsonIgnore
    private static final int DEFAULT_PERIOD_SENDING_PACKET = 20;

    @Schema(hidden = true)
    @JsonIgnore
    // default application identifier
    private static final long DEFAULT_APPLICATION_EUI = 1;


    // A List of MoteSensors representing all sensors on the mote.
    @Schema(description = "The list of sensors on the mote.")
    @JsonIgnore
    private List<MoteSensor> moteSensors;

    // A path representing the connections the mote will follow.
    @Schema(hidden = true)
    @JsonIgnore
    private Path path;

    // The index of the current position in the path
    @Schema(hidden = true)
    @JsonIgnore
    protected int pathPositionIndex;

    // The direction of mote movement along the path (1 or -1)
    @Schema(hidden = true)
    @JsonIgnore
    private int direction;

    // An integer representing the energy level of the mote.
    @Schema(description = "The energy level of the mote.")
    @JsonIgnore
    private int energyLevel;

    // A double representing the movement speed of the mote.
    @Schema(description = "The movement speed of the mote.")
    private double movementSpeed;

    // An integer representing the start offset of the mote in seconds.
    @Schema(hidden = true)
    @JsonIgnore
    private int startMovementOffset;

    // The last used frameCounter used when transmitting lora messages
    @Schema(hidden = true)
    @JsonIgnore
    private short frameCounter;

    // True if the mote can receive a new packet or it has to wait to send a new one before
    @Schema(hidden = true)
    @JsonIgnore
    protected boolean canReceive;

    //id of the trigger to send the keep alive message
    @Schema(hidden = true)
    @JsonIgnore
    private long keepAliveTriggerId;

    @Schema(hidden = true)
    @JsonIgnore
    private LoraWanPacket lastPacketSent;

    // time to await before send the first packet (in seconds)
    @Schema(hidden = true)
    @JsonIgnore
    private int startSendingOffset;

    // period to define how many seconds the mote has to send a packet (in seconds)
    @Schema(hidden = true)
    @JsonIgnore
    private int periodSendingPacket;

    @Schema(hidden = true)
    @JsonIgnore
    private long applicationEUI = DEFAULT_APPLICATION_EUI;

    @Schema(hidden = true)
    @JsonIgnore
    private ReceivedPacketStrategy receivedPacketStrategy;

    @Schema(hidden = true)
    @JsonIgnore
    protected List<ConsumePacketStrategy> consumePacketStrategies;

    // The distance to the nearest gateway
    @Schema(description = "The distance to the nearest gateway.")
    @JsonIgnore
    private double shortestDistanceToGateway = Double.MAX_VALUE;

    // The highest signal power a gateway has received from this mote
    @Schema(description = "The highest signal power a gateway has received from this mote")
    private double highestSignalPower = Double.MIN_VALUE;

    @Schema(description = "The best gateway EUI with highest signal power from last communication")
    @Getter @Setter
    private long bestGatewayEUI = -1;

    @Schema(description = "The distance to the best gateway")
    @Getter @Setter
    private double bestGatewayDistance = Double.MAX_VALUE;

    //endregion

    // region constructor
    /**
     * A constructor generating a node with a given x-coordinate, y-coordinate, environment, transmitting power
     * spreading factor, list of MoteSensors, energy level, connection, sampling rate, movement speed and start offset.
     * @param DevEUI The device's unique identifier
     * @param xPos  The x-coordinate of the node.
     * @param yPos  The y-coordinate of the node.
     * @param SF    The spreading factor of the node.
     * @param transmissionPower The transmitting power of the node.
     * @param moteSensors The mote sensors for this mote.
     * @param energyLevel The energy level for this mote.
     * @param path The path for this mote to follow.
     * @param movementSpeed The movement speed of this mote.
     * @param startMovementOffset The start offset of this mote (in seconds).
     * @param periodSendingPacket period to define how many seconds the mote has to send a packet (in seconds)
     * @param startSendingOffset time to await before send the first packet (in seconds)
     */

    public Mote(long DevEUI, double xPos, double yPos, int transmissionPower,
                int SF, List<MoteSensor> moteSensors, int energyLevel, Path path,
                double movementSpeed, int startMovementOffset, int periodSendingPacket, int startSendingOffset, Environment environment) {
        super(DevEUI, xPos, yPos, transmissionPower, SF, 1.0, environment);
        OverTheAirActivation();
        this.moteSensors = moteSensors;
        this.path = path;
        this.energyLevel = energyLevel;
        this.movementSpeed = movementSpeed;
        this.startMovementOffset = startMovementOffset;
        this.periodSendingPacket = periodSendingPacket;
        this.startSendingOffset = startSendingOffset;

        this.initialize();
    }
    /**
     * A constructor generating a node with a given x-coordinate, y-coordinate, environment, transmitting power
     * spreading factor, list of MoteSensors, energy level, connection, sampling rate and movement speed and  random start offset.
     * @param DevEUI The device's unique identifier
     * @param xPos  The x-coordinate of the node.
     * @param yPos  The y-coordinate of the node.
     * @param SF    The spreading factor of the node.
     * @param transmissionPower The transmitting power of the node.
     * @param moteSensors The mote sensors for this mote.
     * @param energyLevel The energy level for this mote.
     * @param path The path for this mote to follow.
     * @param movementSpeed The movement speed of this mote.
     */

    public Mote(long DevEUI, double xPos, double yPos, int transmissionPower, int SF,
                List<MoteSensor> moteSensors, int energyLevel, Path path, double movementSpeed, Environment environment) {
        this(DevEUI, xPos, yPos, transmissionPower, SF, moteSensors, energyLevel, path, movementSpeed,
            Math.abs((new Random()).nextInt(5)), DEFAULT_PERIOD_SENDING_PACKET, DEFAULT_START_SENDING_OFFSET, environment);
    }


    //endregion
    /**
     * A method describing what the mote should do after successfully receiving a transmission.
     * @param transmission The received transmission.
     */
    @Override
    protected void OnReceive(LoraTransmission transmission) {
        var packet = transmission.getContent();
        //if is a message sent to from a gateway to this mote
        if (canReceive && getEUI() == packet.getReceiverEUI() &&
            this.getEnvironment().getGateways().stream()
                .anyMatch(m -> m.getEUI() == packet.getSenderEUI())) {
            canReceive = false;
            receivedPacketStrategy.addReceivedMessage(packet);
        }
    }

    @Override
    boolean filterLoraSend(NetworkEntity networkEntity, LoraWanPacket packet) {
        return !networkEntity.equals(this);
    }

    @Override
    protected void initialize() {
        this.setPos(this.initialPosition);
        this.pathPositionIndex = getPath().isEmpty() ? -1 : 0;
        this.direction = 1;
        this.frameCounter = 0;
        this.canReceive = false;
        this.keepAliveTriggerId = -1L;
        this.lastPacketSent = null;
        this.receivedPacketStrategy = new MaintainLastPacket();
        this.consumePacketStrategies =  new ArrayList<>();

        resetKeepAliveTrigger(this.startSendingOffset);
    }

    /**
     * a function for the OTAA protocol.
     */
    public void OverTheAirActivation() {
    }

    /**
     * Returns the mote sensors of the mote.
     * @return The mote sensors of the mote.
     */
    public List<MoteSensor> getSensors() {
        return moteSensors;
    }

    /**
     * Returns the path of the mote.
     * @return The path of the mote.
     */
    public Path getPath() {
        return path;
    }


    /**
     * Sets the path of the mote to a given path.
     * @param path The path to set.
     */
    public void setPath(Path path) {
        this.path = path;
    }

    public void setPath(List<GeoPosition> positions) {
        this.path.setPath(positions);
    }

    public int getPathPositionIndex() {
        return pathPositionIndex;
    }

    public GeoPosition getPathPosition() {
        return pathPositionIndex == -1 ?
            getOriginalPos() :
            getPath().getWayPoints().get(pathPositionIndex);
    }

    public Optional<GeoPosition> getNextPathPoint(){
        return getPath().getPoint(getPathPositionIndex()+direction);
    }

    public void reverseDirection(){
        this.direction = -1 * this.direction;
    }

    @Override
    public void setPos(GeoPosition pos) {
        super.setPos(pos);

        getNextPathPoint()
            .ifPresent(p -> {
                var actualPoint = getPos();
                if (MapHelper.equalsGeoPosition(actualPoint, p)) {
                    pathPositionIndex += direction;
                }
            });
    }

    /**
     * Shorten the path of this mote from a given waypoint ID.
     * @param wayPointId The waypoint ID from which the path is shortened (inclusive).
     */
    public void shortenPathFromWayPoint(long wayPointId) {
        this.path.shortenPathFromWayPoint(wayPointId);
    }

    /**
     * Shorten the path of this mote from a given connection ID.
     * @param connectionId The connection ID from which the path is shortened (inclusive).
     */
    public void shortenPathFromConnection(long connectionId) {
        this.path.shortenPathFromConnection(connectionId);
    }


    /**
     *
     * @return ID of application to send the package to
     */
    public long getApplicationEUI() {
        return applicationEUI;
    }

    private void resetKeepAliveTrigger(int offset) {
        GlobalClock clock = this.getEnvironment().getClock();

        if (keepAliveTriggerId != -1L) {
            clock.removeTrigger(keepAliveTriggerId);
        }
        keepAliveTriggerId = clock.addTriggerOneShot(
            clock.getTime().plusSeconds(offset + periodSendingPacket * 5), //TODO configure parameter
            () -> {
                byte[] payload;
                if (lastPacketSent == null) {
                    payload = new byte[]{MessageType.KEEPALIVE.getCode()};
                } else {
                    payload = lastPacketSent.getPayload();
                    payload[0] = MessageType.KEEPALIVE.getCode();
                }
                var packet = new LoraWanPacket(getEUI(), getApplicationEUI(), payload,
                    new BasicFrameHeader().setFCnt(incrementFrameCounter()), new LinkedList<>());
                sendToGateWay(packet);
            }
        );
    }

    /**
     * A function for sending a message with MAC commands to the gateways.
     * @param data The data to send in the message
     * @param macCommands the MAC commands to include in the message.
     */
    public void sendToGateWay(Byte[] data, HashMap<MacCommand, Byte[]> macCommands) {
        sendToGateWay(composePacket(data, macCommands));
    }

    /**
     * A function for sending a packet to the gateways.
     * @param packet the packet to send
     */
    public void sendToGateWay(LoraWanPacket packet) {
        // Send the packet if either:
        //  - It is the first packet ever sent
        //  - It is a heartbeat message
        //  - It does not equal the last transmission (to filter out duplicates)
        if ((lastPacketSent == null && packet.getPayload().length > 0) ||   //is the first packet
            (packet.getPayload().length > 1 &&
                (packet.getPayload()[0] == MessageType.KEEPALIVE.getCode() ||
                    !Arrays.equals(lastPacketSent.getPayload(), packet.getPayload())))) {
            send(packet);
            canReceive = true;
            lastPacketSent = packet;
            resetKeepAliveTrigger(0);
        }
    }

    protected LoraWanPacket composePacket(Byte[] data, Map<MacCommand, Byte[]> macCommands) {
        byte[] payload = new byte[data.length + macCommands.size() + 1];
        payload[0] = MessageType.SENSOR_VALUE.getCode();
        if (payload.length > 1) {
            int i = 1;
            for (MacCommand key : macCommands.keySet()) {
                for (Byte dataByte : macCommands.get(key)) {
                    payload[i] = dataByte;
                    i++;
                }
            }
            for (Byte datum : data) {
                payload[i] = datum;
                i++;
            }
        }
        return new LoraWanPacket(getEUI(), getApplicationEUI(), payload,
            new BasicFrameHeader().setFCnt(incrementFrameCounter()), new LinkedList<>(macCommands.keySet()));
    }

    /**
     * consume all the packet arrived with the strategies previous defined
     */
    public void consumePackets() {
        receivedPacketStrategy.getReceivedPacket().ifPresent(packet -> consumePacketStrategies.forEach(s -> s.consume(this, packet)));
    }

    /**
     * Returns the energy level of the mote.
     * @return The energy level of the mote.
     */
    public int getEnergyLevel() {
        return this.energyLevel;
    }

    /**
     * Sets the energy level of the mote.
     * @param energyLevel The energy level to set.
     */
    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    /**
     * Sets the mote sensors of the mote.
     * @param moteSensors the mote sensors to set.
     */
    public void setSensors(List<MoteSensor> moteSensors) {
        this.moteSensors = moteSensors;
    }

    /**
     * Returns the movementSpeed of the mote.
     * @return The movementSpeed of the mote.
     */
    public double getMovementSpeed() {
        return movementSpeed;
    }

    /**
     * Sets the movement speed of the mote.
     * @param movementSpeed The movement speed of the mote.
     */
    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    /**
     * Returns the start offset of the mote in seconds.
     * @return the start offset of the mote in seconds.
     */
    public int getStartMovementOffset() {
        return this.startMovementOffset;
    }

    public void setStartMovementOffset(int offset) {
        this.startMovementOffset = offset;
    }


    /**
     *
     * @return time to await before send the first packet (in seconds)
     */
    public int getStartSendingOffset() {
        return startSendingOffset;
    }

    public void setStartSendingOffset(int offset) {
        this.startSendingOffset = offset;
    }

    /**
     *
     * @return period to define how many seconds the mote has to send a packet (in seconds)
     */
    public int getPeriodSendingPacket() {
        return periodSendingPacket;
    }

    public void setPeriodSendingPacket(int period) {
        this.periodSendingPacket = period;
    }

    protected short incrementFrameCounter() {
        return frameCounter++;
    }

    public short getFrameCounter() {
        return frameCounter;
    }

    public boolean isArrivedToDestination() {
        if (getPath().isEmpty()) {
            return true;
        }
        //noinspection OptionalGetWithoutIsPresent(if the path is not empty the destination is present)
        return getNextPathPoint().isEmpty();
    }

    public void setShortestDistanceToGateway(double shortestDistanceToGateway) {
        this.shortestDistanceToGateway = shortestDistanceToGateway;
    }

    public double getShortestDistanceToGateway() {
        return shortestDistanceToGateway;
    }

    public void setHighestSignalPower(double highestSignalPower) {
        this.highestSignalPower = highestSignalPower;
    }

    public double getHighestSignalPower() {
        return highestSignalPower;
    }
}

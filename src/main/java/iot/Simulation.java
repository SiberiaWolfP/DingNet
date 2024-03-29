package iot;

import datagenerator.SensorDataGenerator;
import iot.networkentity.Gateway;
import iot.networkentity.Mote;
import iot.networkentity.MoteSensor;
import selfadaptation.feedbackloop.GenericFeedbackLoop;
import util.TimeHelper;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A class representing a simulation.
 */
public class Simulation {

    // region fields
    /**
     * The InputProfile used in the simulation.
     */
    private InputProfile inputProfile;
    /**
     * The Environment used in th simulation.
     */
    private WeakReference<Environment> environment;
    /**
     * The GenericFeedbackLoop used in the simulation.
     */
    private GenericFeedbackLoop approach;

    /**
     * A condition which determines if the simulation should continue (should return {@code false} when the simulation is finished).
     */
    private Predicate<Environment> continueSimulation;

    /**
     * Intermediate parameters used during simulation
     */
    private Map<Mote, LocalDateTime> timeMap;

    // endregion

    // region constructors

    public Simulation() {}

    // endregion

    // region getter/setters

    /**
     * Gets the Environment used in the simulation.
     * @return The Environment used in the simulation.
     */
    public Environment getEnvironment() {
        return environment.get();
    }
    /**
     * Sets the Environment used in the simulation.
     * @param environment  The Environment to use in the simulation.
     */
    public void setEnvironment(WeakReference<Environment> environment) {
        this.environment = environment;
    }

    /**
     * Gets the InputProfile used in the simulation.
     * @return The InputProfile used in the simulation.
     */
    public Optional<InputProfile> getInputProfile() {
        return Optional.ofNullable(inputProfile);
    }
    /**
     * Sets the InputProfile used in the simulation.
     * @param inputProfile  The InputProfile to use in the simulation.
     */
    public void setInputProfile(InputProfile inputProfile) {
        this.inputProfile = inputProfile;
    }

    /**
     * Gets the GenericFeedbackLoop used ine th simulation.
     * @return The GenericFeedbackLoop used in the simulation.
     */
    public GenericFeedbackLoop getAdaptationAlgorithm() {
        return approach;
    }
    /**
     * Sets the GenericFeedbackLoop used in th simulation.
     * @param approach  The GenericFeedbackLoop to use in the simulation.
     */
    public void setAdaptationAlgorithm(GenericFeedbackLoop approach) {
        this.approach = approach;
    }


    public GenericFeedbackLoop getApproach() {
        return approach;
    }
    /**
     * Sets the GenericFeedbackLoop.
     * @param approach The GenericFeedbackLoop to set.
     */
    void setApproach(GenericFeedbackLoop approach) {
        if (getApproach() != null) {
            getApproach().stop();
        }
        this.approach = approach;
        getApproach().start();
    }
    // endregion


    /**
     * Gets the probability with which a mote should be active from the input profile of the current simulation.
     * If no probability is specified, the probability is set to one.
     * Then it performs a pseudo-random choice and sets the mote to active/inactive for the next run, based on that probability.
     */
    private void setupMotesActivationStatus() {
        List<Mote> motes = this.getEnvironment().getMotes();
        Set<Integer> moteProbabilities = this.inputProfile.getProbabilitiesForMotesKeys();
        for (int i = 0; i < motes.size(); i++) {
            Mote mote = motes.get(i);
            double activityProbability = 1;
            if (moteProbabilities.contains(i))
                activityProbability = this.inputProfile.getProbabilityForMote(i);
            if (Math.random() >= 1 - activityProbability)
                mote.enable(true);
        }
    }

    /**
     * Check if all motes have arrived at their destination.
     * @return True if the motes are at their destinations.
     */
    private boolean areAllMotesAtDestination() {
        return this.getEnvironment().getMotes().stream()
            .allMatch(m -> !m.isEnabled() || m.isArrivedToDestination());
    }


    /**
     * Simulate a single step in the simulator.
     */
    public void simulateStep() {
        //noinspection SimplifyStreamApiCallChains
        this.getEnvironment().getMotes().stream()
            .filter(Mote::isEnabled)
            .map(mote -> {
                mote.consumePackets();
                return mote;
            }) //DON'T replace with peek because the filtered mote after this line will not do the consume packet
            .filter(mote -> !mote.isArrivedToDestination())
            .filter(mote ->  1 / mote.getMovementSpeed() <
                this.getEnvironment().getClock().getTime().toEpochSecond(ZoneOffset.UTC) - timeMap.get(mote).toEpochSecond(ZoneOffset.UTC))
            .filter(mote -> this.getEnvironment().getClock().getTime().toEpochSecond(ZoneOffset.UTC) > Math.abs(mote.getStartMovementOffset()))
            .forEach(mote -> {
                timeMap.put(mote, this.getEnvironment().getClock().getTime());
                mote.getNextPathPoint().ifPresent(dst ->
                    this.getEnvironment().moveMote(mote, dst));
            });

        this.getEnvironment().getClock().tick(1);
    }




    public boolean isFinished() {
        return !this.continueSimulation.test(this.getEnvironment());
    }


    private void setupSimulation(Predicate<Environment> pred) {
        this.timeMap = new HashMap<>();

        setupMotesActivationStatus();

        this.getEnvironment().getGateways().forEach(Gateway::reset);

        this.getEnvironment().getMotes().forEach(mote -> {
            // Reset all the sensors of the mote
            mote.getSensors().stream()
                .map(MoteSensor::getSensorDataGenerator)
                .forEach(SensorDataGenerator::reset);

            // Initialize the mote (e.g. reset starting position)
            mote.reset();

            timeMap.put(mote, this.getEnvironment().getClock().getTime());

            // Add initial triggers to the clock for mote data transmissions (transmit sensor readings)
            this.getEnvironment().getClock().addTrigger(LocalDateTime.ofEpochSecond(mote.getStartSendingOffset(),0, ZoneOffset.UTC), () -> {
                mote.sendToGateWay(
                    mote.getSensors().stream()
                        .flatMap(s -> s.getValueAsList(getEnvironment(),mote.getPathPosition(), this.getEnvironment().getClock().getTime()).stream())
                        .toArray(Byte[]::new),
                    new HashMap<>());
                return this.getEnvironment().getClock().getTime().plusSeconds(mote.getPeriodSendingPacket());
            });
        });

        this.continueSimulation = pred;
    }

    void setupSingleRun(boolean shouldResetHistory) {
        if (shouldResetHistory) {
            this.getEnvironment().resetHistory();
        }

        this.setupSimulation((env) -> !areAllMotesAtDestination());
    }

    void setupTimedRun() {
        this.getEnvironment().resetHistory();

        var finalTime = this.getEnvironment().getClock().getTime()
            .plus(inputProfile.getSimulationDuration(), inputProfile.getTimeUnit());

        LocalDateTime reversingTime = this.getEnvironment().getClock().getTime()
            .plus(inputProfile.getRepeatingTime(), inputProfile.getRepeatingTimeTimeUnit());
        this.getEnvironment().getMotes().forEach(mote -> {
            this.getEnvironment().getClock().addTrigger(reversingTime, () -> {
                mote.reverseDirection();
                return this.getEnvironment().getClock().getTime()
                    .plus(inputProfile.getRepeatingTime(), inputProfile.getRepeatingTimeTimeUnit());
            });
        });
        this.setupSimulation((env) -> env.getClock().getTime().isBefore(finalTime));
    }

}

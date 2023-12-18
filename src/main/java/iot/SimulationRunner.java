package iot;

import application.pollution.PollutionGrid;
import application.pollution.PollutionMonitor;
import application.routing.AStarRouter;
import application.routing.RoutingApplication;
import application.routing.heuristic.SimplePollutionHeuristic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gui.MainGUI;
import iot.mqtt.MQTTClientFactory;
import iot.networkentity.*;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jxmapviewer.viewer.GeoPosition;
import org.w3c.dom.Element;
import selfadaptation.adaptationgoals.IntervalAdaptationGoal;
import selfadaptation.adaptationgoals.ThresholdAdaptationGoal;
import selfadaptation.feedbackloop.GenericFeedbackLoop;
import selfadaptation.feedbackloop.ReliableEfficientDistanceGateway;
import selfadaptation.feedbackloop.SignalBasedAdaptation;
import selfadaptation.instrumentation.MoteEffector;
import selfadaptation.instrumentation.MoteProbe;
import util.*;
import util.xml.*;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

public class SimulationRunner {
    private static SimulationRunner instance = null;

    private List<InputProfile> inputProfiles;

    private List<GenericFeedbackLoop> algorithms;
    private QualityOfService QoS;

    private Simulation simulation;
    private Environment environment;
    private List<MoteProbe> moteProbe;
    private PollutionGrid pollutionGrid;

    private RoutingApplication routingApplication;
    private PollutionMonitor pollutionMonitor;
    private NetworkServer networkServer;
    @Getter
    @Setter
    private MutableInteger speed = new MutableInteger(1);

    @Getter
    @Setter
    private long simulationStep = 0;

    public enum SimulationStatus {
        NOT_STARTED,
        RUNNING,
        STOPPED
    }
    @Getter
    @Setter
    private SimulationStatus simulationStatus = SimulationStatus.NOT_STARTED;


    public static SimulationRunner getInstance() {
        if (instance == null) {
            instance = new SimulationRunner();
        }

        return instance;
    }

    private SimulationRunner() {
        QoS = new QualityOfService(new HashMap<>());
        QoS.putAdaptationGoal("reliableCommunication", new IntervalAdaptationGoal(0.0, 0.0));
        QoS.putAdaptationGoal("energyConsumption", new ThresholdAdaptationGoal(0.0));
        QoS.putAdaptationGoal("collisionBound", new ThresholdAdaptationGoal(0.0));

        simulation = new Simulation();
        inputProfiles = loadInputProfiles();

        // Loading all the algorithms
        GenericFeedbackLoop noAdaptation = new GenericFeedbackLoop("No Adaptation") {
            @Override
            public void adapt(Mote mote, Gateway gateway) {

            }
        };

        algorithms = new ArrayList<>();
        algorithms.add(noAdaptation);

        SignalBasedAdaptation signalBasedAdaptation = new SignalBasedAdaptation(QoS);
        algorithms.add(signalBasedAdaptation);

        ReliableEfficientDistanceGateway reliableEfficientDistanceGateway = new ReliableEfficientDistanceGateway();
        algorithms.add(reliableEfficientDistanceGateway);


        /*
         * Setting the mote probes
         */
        moteProbe = new LinkedList<>();
        List<MoteEffector> moteEffector = new LinkedList<>();
        for (int i = 0; i < algorithms.size(); i++) {
            moteProbe.add(new MoteProbe());
            moteEffector.add(new MoteEffector());
        }
        for (GenericFeedbackLoop feedbackLoop : algorithms) {
            feedbackLoop.setMoteProbe(moteProbe.get(algorithms.indexOf(feedbackLoop)));
            feedbackLoop.setMoteEffector(moteEffector.get(algorithms.indexOf(feedbackLoop)));
        }

        networkServer = new NetworkServer(MQTTClientFactory.getSingletonInstance());
        pollutionGrid = new PollutionGrid();
        environment = null;
    }


    // region getters/setters

    public Environment getEnvironment() {
        return this.environment;
    }

    public List<InputProfile> getInputProfiles() {
        return inputProfiles;
    }

    public List<GenericFeedbackLoop> getAlgorithms() {
        return algorithms;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public QualityOfService getQoS() {
        return QoS;
    }

    public RoutingApplication getRoutingApplication() {
        return routingApplication;
    }

    public final PollutionGrid getPollutionGrid() {
        return pollutionGrid;
    }


    public void setApproach(String name) {
        var selectedAlgorithm = algorithms.stream()
            .filter(o -> o.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(String.format("Could not load approach with name %s", name)));

        simulation.setApproach(selectedAlgorithm);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void updateQoS(QualityOfService QoS) {
        this.QoS.updateAdaptationGoals(QoS);
    }

    // endregion


    // region setup simulations

    public void setupSingleRun() {
        this.setupSingleRun(true);
    }

    /**
     * Set the simulator up for a single (regular) run.
     * @param startFresh If true: reset the history stored in the {@link Statistics} class.
     */
    public void setupSingleRun(boolean startFresh) {
        simulation.setupSingleRun(startFresh);

        this.setupSimulationRunner();
    }

    /**
     * Set the simulator up for a single timed run.
     */
    public void setupTimedRun() {
        simulation.setupTimedRun();

        this.setupSimulationRunner();
    }

    /**
     * Setup of applications/servers/clients before each run.
     */
    private void setupSimulationRunner() {
        simulationStatus = SimulationStatus.RUNNING;
        // Remove previous pollution measurements
        pollutionGrid.clean();
        routingApplication.clean();

        // Reset received transmissions in the networkServer
        this.networkServer.reset();
    }


    // endregion


    // region simulations

    /**
     * Check if the simulation has finished, based on the ending condition.
     * @return True if the simulation has finished.
     */
    private boolean isSimulationFinished() {
        if (simulation.isFinished()) {
            simulationStatus = SimulationStatus.STOPPED;
        }
        return simulation.isFinished() || (simulationStatus == SimulationStatus.STOPPED);
    }

    /**
     * Simulate a whole run, until the simulation is finished.
     * @param listener The listener which receives the callbacks every x simulation steps.
     */
    public void simulate(SimulationUpdateListener listener) {
        new Thread(() -> {
            simulationStep = 0;
            while (!this.isSimulationFinished()) {
                this.simulation.simulateStep();

                // Visualize every x seconds
                if (simulationStep++ % (this.speed.intValue() * 1000L) == 0) {
//                    listener.update();
                }
            }

            // Restore the initial positions after the run
            listener.update();
            listener.onEnd();
        }).start();
    }


    @SuppressWarnings("unused")
    public void totalRun() {
        this.totalRun(o -> {});
    }

    /**
     * Simulate (and keep track of) multiple runs with the simulator.
     * @param fn A callback function which is invoked after every executed single run.
     */
    public void totalRun(@NotNull Consumer<Pair<Integer, Integer>> fn) {
        int nrOfRuns = simulation.getInputProfile()
            .orElseThrow(() -> new IllegalStateException("No input profile selected before running the simulation"))
            .getNumberOfRuns();
        setupSingleRun(true);

        new Thread(() -> {
            fn.accept(new Pair<>(0, nrOfRuns));

            for (int i = 0; i < nrOfRuns; i++) {

                while(!simulation.isFinished()) {
                    this.simulation.simulateStep();
                }

                fn.accept(new Pair<>(i + 1, nrOfRuns));

                if (i != nrOfRuns - 1) {
                    this.getEnvironment().addRun();
                    setupSingleRun(false);
                }
            }
        }).start();
    }

    // endregion


    // region loading/saving/cleanup

    public void updateInputProfilesFile() {
        File file = new File(MainGUI.class.getResource(Constants.INPUT_PROFILES_FILE).getPath());
        updateInputProfilesFile(file);
    }

    public void updateInputProfilesFile(File file) {
        InputProfilesWriter.updateInputProfilesFile(inputProfiles, file);
    }

    private List<InputProfile> loadInputProfiles() {
        return InputProfilesReader.readInputProfiles();
    }

    /**
     * Load a configuration from a provided xml file.
     * @param file The file with the configuration.
     */
    public void loadConfigurationFromFile(File file) {
        this.cleanupSimulation();

        ConfigurationReader.loadConfiguration(file, this);
        simulation.setEnvironment(new WeakReference<>(this.getEnvironment()));

        for (Gateway gateway : simulation.getEnvironment().getGateways()) {
            for (int i = 0; i < algorithms.size(); i++) {
                gateway.addSubscription(moteProbe.get(i));
            }
        }

        setupApplications();
    }

    public void resetGateways(int numberOfGateways) {
        Random random = new Random();
        int width = this.getEnvironment().getMaxXpos();
        int height = this.getEnvironment().getMaxYpos();

        this.getEnvironment().getGateways().clear();

        // Make random gateways
        for (int i = 0; i < numberOfGateways; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            long devEUI = random.nextLong(Long.MAX_VALUE);
            Gateway gateway = new Gateway(devEUI, x, y, 12, 12, this.getEnvironment());
            this.getEnvironment().addGateway(gateway);
        }
    }

    public void resetMote() {
        // Make random map characteristics
        Random random = new Random();
        int width = this.getEnvironment().getMaxXpos();
        int height = this.getEnvironment().getMaxYpos();

        this.getEnvironment().getMotes().clear();

        // Make random path
        int numberOfWaypoints = 100;
        Map<Long, GeoPosition> waypoints = new HashMap<>();
        Map<Long, Connection> connections = new HashMap<>();
        Path path = new Path(this.getEnvironment().getGraph());
        for (int i = 0; i < numberOfWaypoints; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            GeoPosition geoPosition = this.getEnvironment().getMapHelper().toGeoPosition(x, y);
            waypoints.put((long) i, geoPosition);
            path.addPosition(geoPosition);
        }
        for (int i = 0; i < numberOfWaypoints - 1; i++) {
            connections.put((long) i, new Connection(i, (long) i + 1));
        }
        this.getEnvironment().setGraph(new GraphStructure(waypoints, connections));

        // Make random mote
        Pair<Double, Double> pos = this.getEnvironment().getMapHelper().toMapCoordinate(waypoints.get((long)0));
        long devEUI = random.nextLong(Long.MAX_VALUE);
        LinkedList<MoteSensor> sensors = new LinkedList<>();
        sensors.add(MoteSensor.valueOf("SOOT"));
        Mote mote = MoteFactory.createMote(devEUI, pos.getLeft(), pos.getRight(), 12, 12,
            sensors, 100, path, 1, 1,
            20, 1, this.getEnvironment());
        this.getEnvironment().addMote(mote);

        for (Gateway gateway : simulation.getEnvironment().getGateways()) {
            for (int i = 0; i < algorithms.size(); i++) {
                gateway.addSubscription(moteProbe.get(i));
            }
        }
    }

    public void resetMap() {
        Random random = new Random();
        int width = this.getEnvironment().getMaxXpos();
        int height = this.getEnvironment().getMaxYpos();

        Characteristic[][] characteristics = this.getEnvironment().getCharacteristics();

        int numberOfZones = this.getEnvironment().getNumberOfZones();
        long n = Math.round(Math.sqrt(numberOfZones));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Characteristic characteristic = Characteristic.values()[random.nextInt(Characteristic.values().length)];

                double widthSize = ((double) width) / n;
                double heightSize = ((double) height) / n;
                for (int x = (int) Math.round(j * widthSize); x < (int) Math.round((j + 1) * widthSize); x++) {
                    for (int y = (int) Math.round(i * heightSize); y < (int) Math.round((i + 1) * heightSize); y++) {
                        characteristics[x][y] = characteristic;
                    }
                }
            }
        }

        this.getEnvironment().setCharacteristics(characteristics);
    }

    public void reconnect() {
        for (Gateway gateway : simulation.getEnvironment().getGateways()) {
            for (int i = 0; i < algorithms.size(); i++) {
                gateway.addSubscription(moteProbe.get(i));
            }
        }
    }

    /**
     * Save a configuration to a file.
     * @param file The file to save to.
     */
    public void saveConfigurationToFile(File file) {
        ConfigurationWriter.saveConfigurationToFile(file, this);
    }


    public void saveSimulationToFile(File file) {
        SimulationWriter.saveSimulationToFile(file, simulation);
    }


    /**
     * Method which is called when a new configuration will be opened.
     * Provides manual cleanup for old applications/servers/clients/...
     */
    private void cleanupSimulation() {
        if (this.pollutionMonitor != null) {
            this.pollutionMonitor.destruct();
        }
        if (this.routingApplication != null) {
            this.routingApplication.destruct();
        }

        this.networkServer.reconnect();
        this.environment = null;
    }

    /**
     * Initialize all applications used in the simulation.
     */
    private void setupApplications() {
        this.pollutionMonitor = new PollutionMonitor(this.getEnvironment(), this.pollutionGrid);
        this.routingApplication = new RoutingApplication(
            new AStarRouter(new SimplePollutionHeuristic(pollutionGrid)), getEnvironment().getGraph(), environment
        );
    }

    // endregion
}

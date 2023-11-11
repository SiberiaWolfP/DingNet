package webserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import iot.SimulationRunner;
import iot.networkentity.Gateway;
import iot.networkentity.Mote;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "The monitor object with all the necessary information about the simulation for the adaptation")
public class MonitorDTO {

    @Schema(description = "The status of the simulation.")
    private SimulationRunner.SimulationStatus status;

    @Schema(description = "The list of motes in the simulation.")
    private List<Mote> motes;

    @Schema(description = "The list of gateways in the simulation.")
    private List<Gateway> gateways;

    @Schema(description = "The list of adaptation approaches in the simulation.")
    private List<AlgorithmDTO> adaptationApproaches;

    @Schema(description = "The list of adaptation goals in the simulation.")
    private List<QoSOptionsDTO> adaptationGoals;
}

package webserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import iot.networkentity.Gateway;
import iot.networkentity.Mote;
import lombok.Data;
import selfadaptation.adaptationgoals.AdaptationGoal;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "The monitor object which contains all the motes in the simulation.")
public class MonitorDTO {

    @Schema(description = "The list of motes in the simulation.")
    private List<Mote> motes;

    @Schema(description = "The list of gateways in the simulation.")
    private List<Gateway> gateways;

    @Schema(description = "The list of adaptation goals in the simulation.")
    private Map<String, AdaptationGoal> adaptationGoals;
}

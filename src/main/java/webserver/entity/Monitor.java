package webserver.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import iot.networkentity.Gateway;
import iot.networkentity.Mote;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@Schema(description = "The monitor object which contains all the motes in the simulation.")
public class Monitor {

    @Schema(description = "The list of motes in the simulation.")
    private List<Mote> motes;
}

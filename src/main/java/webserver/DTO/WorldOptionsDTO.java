package webserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WorldOptionsDTO {

    @Schema(description = "The name of the configuration file.")
    String configName;

    @Schema(description = "Speed of the simulation.")
    Integer speed;

    @Schema(description = "The number of gateways, if mock mode is enabled. That is, configName is 'mock'.")
    Integer numberOfGateways;

    @Schema(description = "Whether the map characteristics should be refreshed, if mock mode is enabled. That is, configName is 'mock'.")
    Boolean mapRefresh = false;
}

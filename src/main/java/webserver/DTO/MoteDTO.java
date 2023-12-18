package webserver.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MoteDTO {

    @Schema(description = "The mote's EUI.")
    @JsonProperty("EUI")
    private long EUI;
    @Schema(description = "The mote's latitude")
    private double latitude;
    @Schema(description = "The mote's longitude")
    private double longitude;
    @Schema(description = "The mote's transmission power setting.")
    private double transmissionPower;
    @Schema(description = "The mote's movement speed.")
    private double movementSpeed;
    @Schema(description = "The mote's best communication signal strength with gateways.")
    private double bestSignalStrength;
    @Schema(description = "Distance to the gateway with the best signal strength.")
    private double bestGatewayDistance;
    @Schema(description = "The gateway's latitude with the best signal strength.")
    private double bestGatewayLatitude;
    @Schema(description = "The gateway's longitude with the best signal strength.")
    private double bestGatewayLongitude;
    @Schema(description = "Path loss in the mote's current position.")
    private double pathLoss;
    @Schema(description = "Shadow fading in the mote's current position.")
    private double shadowFading;
}

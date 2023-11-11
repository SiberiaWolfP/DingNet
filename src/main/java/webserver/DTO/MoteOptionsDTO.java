package webserver.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MoteOptionsDTO {
    @Schema(description = "The mote's EUI.")
    @JsonProperty("EUI")
    private long EUI;

    @Schema(description = "The mote's transmission power.")
    private int transmissionPower;

    public long getEUI() {
        return EUI;
    }
}

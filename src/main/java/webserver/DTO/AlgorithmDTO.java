package webserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AlgorithmDTO {
    @Schema(description = "The name of the adaption algorithm.")
    String name;

    @Schema(description = "The boolean indicating whether the algorithm is active.")
    boolean active;
}

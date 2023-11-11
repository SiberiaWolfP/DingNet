package webserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "The configuration of an adaptation.")
public class ExecuteDTO {

    @Schema(description = "The adaptation approaches used in the adaptation")
    private String adaptationApproaches;

    @Schema(description = "The adaptation goal for adaptation.")
    private QoSOptionsDTO adaptationGoals;

    @Schema(description = "The list of motes that will be adapted with their options. " +
        "With list multiple motes can be adapted at the same time.")
    private List<MoteOptionsDTO> moteOptions;
}

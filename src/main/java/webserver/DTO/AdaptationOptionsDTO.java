package webserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdaptationOptionsDTO {

    @Schema(description = "The name of the feedback loop.")
    FeedbackLoop feedbackLoop;
}

@Data
class FeedbackLoop {
    @Schema(description = "The name of the feedback loop.")
    String name;

    @Schema(description = "Indicates if the feedback loop is currently active.")
    Boolean active;
}

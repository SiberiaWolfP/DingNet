package webserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import selfadaptation.adaptationgoals.AdaptationGoal;
import selfadaptation.adaptationgoals.IntervalAdaptationGoal;
import selfadaptation.adaptationgoals.ThresholdAdaptationGoal;

@Data
public class QoSOptionsDTO {
    @Schema(description = "The name of the adaptation goal.")
    String name;

    @Schema(description = "The adaptation goal.",
        oneOf = {IntervalAdaptationGoal.class, ThresholdAdaptationGoal.class})
    AdaptationGoal adaptationGoal;

    public void setAdaptationGoal(AdaptationGoal adaptationGoal) {
        this.adaptationGoal = adaptationGoal;
    }
}

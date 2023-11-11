package selfadaptation.adaptationgoals;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import webserver.util.AdaptionGoalDeserializer;

/**
 * An abstract class representing an adaptation goal.
 */
@JsonDeserialize(using = AdaptionGoalDeserializer.class)
public abstract class AdaptationGoal {

    /**
     * A boolean representing whether the goal is enabled or not.
     */
    @Getter
    @Setter
    @Schema(description = "A boolean representing whether the goal is enabled or not.")
    boolean enabled = false;
}

package selfadaptation.adaptationgoals;

import lombok.Getter;
import lombok.Setter;

/**
 * An abstract class representing an adaptation goal.
 */
public abstract class AdaptationGoal {

    /**
     * A boolean representing whether the goal is enabled or not.
     */
    @Getter
    @Setter
    boolean enabled = false;
}

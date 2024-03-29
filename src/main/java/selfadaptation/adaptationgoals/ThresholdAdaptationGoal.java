package selfadaptation.adaptationgoals;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * An adaptation goal with a threshold.
 */
public class ThresholdAdaptationGoal extends AdaptationGoal {
    /**
     * The threshold of the goal.
     */
    @Schema(description = "The threshold of the goal.")
    private final double threshold;

    /**
     * A constructor generating a ThresholdAdaptationGoal with given threshold.
     * @param threshold
     */
    public ThresholdAdaptationGoal(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Returns the threshhold.
     * @return
     */
    public double getThreshold() {
        return threshold;
    }


    public String toString() {
        return Double.toString(this.threshold);
    }
}

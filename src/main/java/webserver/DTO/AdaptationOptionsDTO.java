package webserver.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Schema(description = "Configurations can be set for the adaptation.")
public class AdaptationOptionsDTO {

//    @Schema(description = "The adaptation approaches used in the adaptation")
//    private DiscreteOptions<String> adaptationApproaches = new DiscreteOptions<>(){{
//        setValues(Arrays.asList("No Adaptation", "Signal-based", "Distance-based"));
//    }};
//
//    @Schema(description = "The adaptation goal for adaptation.")
//    private List<QoSOption> adaptationGoals = new ArrayList<>(){{
//        add(new QoSOption(){{
//            setName("reliableCommunication");
//            setAdaptationGoal(new IntervalAdaptationGoalOption(){{
//                setLowerBoundary(new ContinuousOptions<Double>(){{
//                    setStart(-100.0);
//                    setStop(20.0);
//                }});
//                setUpperBoundary(new ContinuousOptions<Double>(){{
//                    setStart(-100.0);
//                    setStop(20.0);
//                }});
//            }});
//        }});
//    }};

    @Schema(description = "The configuration of a mote that can be adapted.")
    private MoteOptions moteOptions = new MoteOptions();
}

@Data
class DiscreteOptions<T> {

    @Schema(description = "The list of values that can be chosen from.")
    private List<T> values;

    @Schema(description = "The domain of the values. " +
        "If the domain is discrete, the values can be chosen from the list. " +
        "If the domain is continuous, the values can be chosen from the interval.")
    private String domain = "discrete";
}

@Data
class ContinuousOptions<T> {

    @Schema(description = "The start value of the interval.")
    private T start;

    @Schema(description = "The stop value of the interval.")
    private T stop;

    @Schema(description = "The domain of the values. " +
        "If the domain is discrete, the values can be chosen from the list. " +
        "If the domain is continuous, the values can be chosen from the interval.")
    private String domain = "continuous";
}

@Data
class QoSOption {

    @Schema(description = "The name of the adaptation goal.")
    private String name;

    @Schema(description = "The adaptation goal.",
        oneOf = {IntervalAdaptationGoalOption.class, ThresholdAdaptationGoalOption.class})
    private Object adaptationGoal;
}

@Data
class IntervalAdaptationGoalOption {

    @Schema(description = "Lower boundary of the interval.")
    private ContinuousOptions<Double> lowerBoundary;

    @Schema(description = "Upper boundary of the interval.")
    private ContinuousOptions<Double> upperBoundary;
}

@Data
class ThresholdAdaptationGoalOption {

    @Schema(description = "The threshold of the adaptation goal.")
    private ContinuousOptions<Double> threshold;
}

@Data
class MoteOptions {
    @Schema(description = "The mote's EUI.")
    @JsonProperty("EUI")
    private long EUI;

    @Schema(description = "The mote's transmission power.")
    private DiscreteOptions<Integer> transmissionPower = new DiscreteOptions<>(){{
        setValues(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }};
}

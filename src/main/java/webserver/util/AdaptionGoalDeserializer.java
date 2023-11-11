package webserver.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import selfadaptation.adaptationgoals.AdaptationGoal;
import selfadaptation.adaptationgoals.IntervalAdaptationGoal;
import selfadaptation.adaptationgoals.ThresholdAdaptationGoal;

import java.io.IOException;

public class AdaptionGoalDeserializer extends JsonDeserializer<AdaptationGoal> {
    @Override
    public AdaptationGoal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);

        if (node.has("lowerBoundary") && node.has("upperBoundary")) {
            return new IntervalAdaptationGoal(
                node.get("lowerBoundary").asDouble(), node.get("upperBoundary").asDouble());
        } else if (node.has("threshold")) {
            return new ThresholdAdaptationGoal(node.get("threshold").asDouble());
        }
        throw new JsonProcessingException("Cannot determine subclass") {};
    }
}

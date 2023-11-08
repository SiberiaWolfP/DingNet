package webserver.service;

import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.swagger2.Swagger2Module;
import org.springframework.stereotype.Service;
import webserver.entity.Monitor;

@Service
public class SchemaService {

    final SchemaGenerator generator;

    public SchemaService() {
        Swagger2Module module = new Swagger2Module();
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09)
            .with(module)
            .without(Option.GETTER_METHODS)
            .without(Option.VOID_METHODS)
            .without(Option.STATIC_METHODS)
            .without(Option.NONSTATIC_NONVOID_NONGETTER_METHODS)
            .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)
            .with(Option.FLATTENED_ENUMS_FROM_TOSTRING);

        generator = new SchemaGenerator(configBuilder.build());
    }

    public String getMonitorSchema() {
        return generator.generateSchema(Monitor.class).toPrettyString();
    }

    public String getAdaptationOptionsSchema() {
        return "adaptation_options_schema";
    }

    public String getExecuteSchema() {
        return "execute_schema";
    }
}

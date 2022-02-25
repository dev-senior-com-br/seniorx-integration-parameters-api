package br.com.senior.seniorx.integration.parameter;

import org.apache.camel.component.jackson.JacksonDataFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(serialization = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class GetParametersInput {

    public static final JacksonDataFormat GET_PARAMETERS_INPUT_FORMAT = new JacksonDataFormat(GetParametersInput.class);

    @JsonProperty("name")
    public String name;

    @Override
    public String toString() {
        return "GetParametersInput [name=" + name + "]";
    }

}

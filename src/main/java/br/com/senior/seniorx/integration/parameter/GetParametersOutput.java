package br.com.senior.seniorx.integration.parameter;

import java.util.List;

import org.apache.camel.component.jackson.JacksonDataFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(serialization = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class GetParametersOutput {

    public static final JacksonDataFormat GET_PARAMETERS_OUTPUT_FORMAT = new JacksonDataFormat(GetParametersOutput.class);

    // Success response

    @JsonProperty("parameters")
    public List<Parameter> parameters;

    // Error response

    @JsonProperty("message")
    public String message;
    @JsonProperty("reason")
    public String reason;

    @Override
    public String toString() {
        return "GetParametersOutput [parameters=" + parameters + ", message=" + message + ", reason=" + reason + "]";
    }

}

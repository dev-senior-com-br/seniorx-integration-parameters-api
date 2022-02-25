package br.com.senior.seniorx.integration.parameter;

import static java.lang.Boolean.TRUE;

import org.apache.camel.component.jackson.JacksonDataFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(serialization = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Parameter {

    public static final JacksonDataFormat PARAMETER_FORMAT = new JacksonDataFormat(Parameter.class);

    @JsonProperty("key")
    public String key;
    @JsonProperty("value")
    public String value;
    @JsonProperty("secret")
    public Boolean secret;

    @Override
    public String toString() {
        String val = TRUE.equals(secret) ? "?" : value;
        return "Parameter [key=" + key + ", value=" + val + "]";
    }

}

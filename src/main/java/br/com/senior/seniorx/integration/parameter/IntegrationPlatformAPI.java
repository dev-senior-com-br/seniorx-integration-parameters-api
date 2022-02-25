package br.com.senior.seniorx.integration.parameter;

import static br.com.senior.seniorx.http.camel.PrimitiveType.QUERY;
import static br.com.senior.seniorx.integration.parameter.GetParametersInput.GET_PARAMETERS_INPUT_FORMAT;
import static br.com.senior.seniorx.integration.parameter.GetParametersOutput.GET_PARAMETERS_OUTPUT_FORMAT;
import static org.apache.camel.ExchangePattern.InOut;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;

import br.com.senior.seniorx.http.camel.SeniorXHTTPRouteBuilder;

public class IntegrationPlatformAPI {

    private static final String PLATFORM = "platform";
    private static final String INTEGRATION_PLATFORM = "integration_platform";

    private static final String LOAD_PARAMETERS = "load-parameters";
    private static final String HEADERS_LOG = "${in.headers}";

    private final RouteBuilder builder;
    private final String integrationName;
    private final UUID id = UUID.randomUUID();
    private final String route = "direct:seniorx-integration-platform-" + id.toString();
    private final String to = "direct:seniorx-integration-platform-response-" + id.toString();

    public IntegrationPlatformAPI(RouteBuilder builder, String integrationName) {
        this.builder = builder;
        this.integrationName = integrationName;
    }

    public String route() {
        return route;
    }

    public String responseRoute() {
        return to;
    }

    public void prepare() {
        SeniorXHTTPRouteBuilder getParameters = new SeniorXHTTPRouteBuilder(builder);
        getParameters //
        .method("post") //
        .domain(PLATFORM) //
        .service(INTEGRATION_PLATFORM) //
        .primitiveType(QUERY) // .
        .primitive("getParameters");

        builder //
        .from(route) //
        .routeId(LOAD_PARAMETERS) //
        .to("log:load-parameters") //
        .log(HEADERS_LOG) //

        .process(this::prepareGetParameters) //
        .marshal(GET_PARAMETERS_INPUT_FORMAT) //
        .to("log:get-parameters") //
        .log(HEADERS_LOG) //
        .setExchangePattern(InOut) //
        .process(getParameters::route) //
        .unmarshal(GET_PARAMETERS_OUTPUT_FORMAT) //
        .to("log:parameters-retrieved") //
        .log(HEADERS_LOG) //

        .process(this::loadParameters) //
        .to(to) //
        ;
    }

    private void prepareGetParameters(Exchange exchange) {
        Message message = exchange.getMessage();

        if (message.getHeader("Authorization") == null) {
            throw new IntegrationPlatformException("Not authencitaced");
        }

        GetParametersInput body = new GetParametersInput();
        body.name = integrationName;

        message.setBody(body);
    }

    private void loadParameters(Exchange exchange) {
        Exception exception = exchange.getException();
        if (exception != null) {
            throw new IntegrationPlatformException(exception);
        }
        GetParametersOutput output = (GetParametersOutput) exchange.getMessage().getBody();
        if (output.parameters == null) {
            throw new IntegrationPlatformException(output.reason + ": " + output.message);
        }
        output.parameters.forEach(parameter -> exchange.setProperty(parameter.key, parameter.value));
    }

}

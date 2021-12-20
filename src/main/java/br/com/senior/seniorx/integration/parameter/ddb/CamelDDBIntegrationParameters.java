package br.com.senior.seniorx.integration.parameter.ddb;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.spi.PropertiesComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

import br.com.senior.seniorx.integration.parameter.IntegrationParameters;

public class CamelDDBIntegrationParameters implements IntegrationParameters {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelDDBIntegrationParameters.class);

    private final Exchange exchange;
    private final AmazonDynamoDB ddb;
    private final String table;
    private String integrationName;

    public CamelDDBIntegrationParameters(Exchange exchange, String integrationName) {
        this.exchange = exchange;
        this.integrationName = integrationName;
        this.ddb = connectToDynamoDB();
        this.table = getParametersTable();
    }

    @Override
    public boolean getBoolean(String name) {
        Optional<AttributeValue> value = get(name);
        if (value.isPresent()) {
            return Boolean.TRUE.equals(value.get().getBOOL());
        }
        return false;
    }

    @Override
    public String getString(String name) {
        Optional<AttributeValue> value = get(name);
        if (value.isPresent()) {
            return value.get().getS();
        }
        return null;
    }

    private Optional<AttributeValue> get(String parameter) {
        Object selector = exchange.getIn().getHeader("selector");
        if (selector == null) {
            LOGGER.error("Selector header not found.");
            return Optional.empty();
        }
        String tenant = selector.toString();

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Tenant", new AttributeValue(tenant));
        String keyName = integrationName + ':' + parameter;
        key.put("Key", new AttributeValue(keyName));

        GetItemRequest request = new GetItemRequest().withKey (key).withTableName(table);
        Map<String, AttributeValue> item = ddb.getItem(request).getItem();
        if (item == null) {
            LOGGER.info("Parameter {} not found for tenant {} and integration {} (Key: {}).", parameter, tenant, integrationName, keyName);
            return Optional.empty();
        }
        return Optional.ofNullable(item.get("Value"));
    }

    private AmazonDynamoDB connectToDynamoDB() {
        PropertiesComponent properties = exchange.getContext().getPropertiesComponent();
        String awsAccessKey = properties.resolveProperty("integration.aws.access.key").orElse(null);
        String awsSecretKey = properties.resolveProperty("integration.aws.secret.key").orElse(null);
        String region = properties.resolveProperty("integration.ddb.region").orElse("sa-east-1");
        return AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey))).withRegion(region).build();
    }

    private String getParametersTable() {
        return exchange.getProperty("integration.ddb.parameters.table", "IntegrationParameters", String.class);
    }

}

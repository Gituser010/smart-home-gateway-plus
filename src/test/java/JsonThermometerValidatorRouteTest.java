import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonThermometerValidatorRouteTest extends CamelTestSupport {

    @EndpointInject("mock:result")
    MockEndpoint result;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("json-validator:classpath:schemas/thermometerSchema.json")
                        .to("mock:result");
            }
        };
    }

    @Test
    public void testThermometerSchemaOk() throws InterruptedException {
        String json = "{\"deviceType\":\"thermometer\",\"label\":\"device1\",\"temperature\":25.0,\"enabled\":true,\"unit\":\"C\"}";

        result.expectedMessageCount(1);

        template.sendBody("direct:start", json);

        result.assertIsSatisfied();
    }

    @Test
    public void testThermometerSchemaMinimalOk() {
        String json = "{\"deviceType\":\"thermometer\",\"label\":\"device1\",\"temperature\":25.0}";

        template.sendBody("direct:start", json);

        assertTrue(result.getExchanges().get(0).getIn().getBody().toString().contains("deviceType"));
    }

    @Test
    public void testThermometerSchemaWrongTemperature() {
        String json = "{\"deviceType\":\"thermometer\",\"label\":\"device1\",\"temperature\":\"25.0\",\"enabled\":true,\"unit\":\"C\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaWrongUnit() {
        String json = "{\"deviceType\":\"thermometer\",\"label\":\"device1\",\"temperature\":25.0,\"enabled\":true,\"unit\":1}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaMissingName() throws InterruptedException {
        String json = "{\"deviceType\":\"thermometer\",\"temperature\":25.0}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaMissingDeviceType() throws InterruptedException {
        String json = "{\"label\":\"device1\",\"temperature\":25.0,\"enabled\":true,\"unit\":\"C\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaMissingAll() throws InterruptedException {
        String json = "{}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaEnabledNotBoolean() throws InterruptedException {
        String json = "{\"deviceType\":\"thermometer\",\"label\":\"device1\",\"temperature\":25.0,\"enabled\":\"true\",\"unit\":\"C\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaTemperatureNotNumber() throws InterruptedException {
        String json = "{\"deviceType\":\"thermometer\",\"label\":\"device1\",\"temperature\":\"25.0\",\"enabled\":true,\"unit\":\"C\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaUnitNotString() throws InterruptedException {
        String json = "{\"deviceType\":\"thermometer\",\"label\":\"device1\",\"temperature\":25.0,\"enabled\":true,\"unit\":1}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaLabelNotString() throws InterruptedException {
        String json = "{\"deviceType\":\"thermometer\",\"label\":1,\"temperature\":25.0,\"enabled\":true,\"unit\":\"C\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaDeviceTypeNotString() throws InterruptedException {
        String json = "{\"deviceType\":1,\"label\":\"device1\",\"temperature\":25.0,\"enabled\":true,\"unit\":\"C\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testThermometerSchemaExtraField() throws InterruptedException {
        String json = "{\"deviceType\":\"thermometer\",\"label\":\"device1\",\"temperature\":25.0,\"enabled\":true,\"unit\":\"C\",\"extraField\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }
}

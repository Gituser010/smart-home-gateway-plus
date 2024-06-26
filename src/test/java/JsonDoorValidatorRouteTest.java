import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonDoorValidatorRouteTest extends CamelTestSupport {
    @EndpointInject("mock:result")
    MockEndpoint result;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("json-validator:classpath:schemas/doorSchema.json")
                        .to("mock:result");
            }
        };
    }

    @Test
    public void testDoorSchemaOk() throws InterruptedException {
        String json = "{\"deviceType\":\"door\",\"label\":\"device1\",\"status\":\"opened\",\"enabled\":true}";

        result.expectedMessageCount(1);

        template.sendBody("direct:start", json);

        result.assertIsSatisfied();
    }

    @Test
    public void testDoorSchemaMinimalOk() {
        String json = "{\"deviceType\":\"door\",\"label\":\"device1\"}";

        template.sendBody("direct:start", json);

        assertTrue(result.getExchanges().get(0).getIn().getBody().toString().contains("deviceType"));
    }

    @Test
    public void testDoorIncorrectStatus() {
        String json = "{\"deviceType\":\"door\",\"label\":\"device1\",\"status\":\"ano\",\"enabled\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testDoorSchemaMissingName() throws InterruptedException {
        String json = "{\"deviceType\":\"door\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testDoorSchemaMissingDeviceType() throws InterruptedException {
        String json = "{\"label\":\"device1\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testDoorSchemaWrongStatus() {
        String json = "{\"deviceType\":\"door\",\"label\":\"device1\",\"status\":1,\"enabled\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testDoorSchemaWrongEnabled() {
        String json = "{\"deviceType\":\"door\",\"label\":\"device1\",\"status\":\"opened\",\"enabled\":\"true\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testDoorSchemaWrongDeviceType() {
        String json = "{\"deviceType\":1,\"label\":\"device1\",\"status\":\"opened\",\"enabled\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testDoorSchemaWrongLabel() {
        String json = "{\"deviceType\":\"door\",\"label\":1,\"status\":\"opened\",\"enabled\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }


    @Test
    public void testDoorSchemaMissingAll() throws InterruptedException {
        String json = "{}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testDoorSchemaExtraField() throws InterruptedException {
        String json = "{\"deviceType\":\"door\",\"label\":\"device1\",\"status\":\"ON\",\"extra\":\"extra\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }
}

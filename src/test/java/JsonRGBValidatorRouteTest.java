import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonRGBValidatorRouteTest extends CamelTestSupport {

    @EndpointInject("mock:result")
    MockEndpoint result;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("json-validator:classpath:schemas/rgbLightSchema.json")
                        .to("mock:result");
            }
        };
    }

    @Test
    public void testRGBSchemaOk() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":255,\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        result.expectedMessageCount(1);

        template.sendBody("direct:start", json);

        result.assertIsSatisfied();
    }

    @Test
    public void testRGBSchemaMinimalOk() {
        String json = "{\"deviceType\":\"RGB\",\"label\":\"device1\"}";

        template.sendBody("direct:start", json);

        assertTrue(result.getExchanges().get(0).getIn().getBody().toString().contains("deviceType"));
    }

    @Test
    public void testRGBSchemaMissingName() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaMissingDeviceType() throws InterruptedException {
        String json = "{\"label\":\"device1\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaMissingAll() throws InterruptedException {
        String json = "{}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaExtraField() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"extra\":\"extra\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaRedOutOfRange() throws InterruptedException {
        String maxJson = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":256,\"green\":255,\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", maxJson));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
        String minJson = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":-1,\"green\":255,\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", minJson));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaGreenOutOfRange() throws InterruptedException {
        String maxJson = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":256,\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", maxJson));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
        String minJson = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":-1,\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", minJson));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaBlueOutOfRange() throws InterruptedException {
        String maxJson = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":255,\"blue\":256,\"enabled\":true,\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", maxJson));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
        String minJson = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":255,\"blue\":-1,\"enabled\":true,\"switchedOn\":true}";

        thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", minJson));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaSwitchedOnNotBoolean() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":255,\"blue\":255,\"enabled\":true,\"switchedOn\":\"true\"}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaEnabledNotBoolean() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":255,\"blue\":255,\"enabled\":\"true\",\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaRedNotNumber() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":\"255\",\"green\":255,\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaGreenNotNumber() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":\"255\",\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaBlueNotNumber() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\",\"label\":\"device1\",\"red\":255,\"green\":255,\"blue\":\"255\",\"enabled\":true,\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaLabelNotString() throws InterruptedException {
        String json = "{\"deviceType\":\"RGB\",\"label\":1,\"red\":255,\"green\":255,\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

    @Test
    public void testRGBSchemaDeviceTypeNotString() throws InterruptedException {
        String json = "{\"deviceType\":1,\"label\":\"device1\",\"red\":255,\"green\":255,\"blue\":255,\"enabled\":true,\"switchedOn\":true}";

        CamelExecutionException thrown = assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:start", json));
        assertTrue(thrown.getCause() instanceof JsonValidationException);
    }

}

package io.patriotframework.smarthomeplusgateway;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.http.NettyHttpOperationFailedException;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

import static io.patriotframework.smarthomeplusgateway.DeviceConstants.*;

/**
 * GatewayRouteBuilder
 *
 * This class is responsible for creating the routes for the gateway.
 */

@Component
public class GatewayRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();


        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .setBody(simple("{\"error\": \"${exception.message}}\"}"))
                .log(LoggingLevel.ERROR, "Error occurred: ${exception.stacktrace}");

        from("direct:validate")
                .log(LoggingLevel.INFO, "Validating device ${body}")
                .choice()
                .when(simple("${body} == null"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("{\"error\": \"Body cant be null\"}"))
                .choice()
                .when(simple("${body[deviceType]} == '" + FIREPLACE_DEVICE_TYPE + "'"))
                .marshal().json()
                .to("json-validator:schemas/fireplaceSchema.json")
                .when(simple("${body[deviceType]} == '" + DOOR_DEVICE_TYPE + "'"))
                .marshal().json()
                .to("json-validator:schemas/DoorSchema.json")
                .when(simple("${body[deviceType]} == '" + RGB_DEVICE_TYPE + "'"))
                .marshal().json()
                .to("json-validator:schemas/rgbLightSchema.json")
                .when(simple("${body[deviceType]} == '" + THERMOMETER_DEVICE_TYPE + "'"))
                .marshal().json()
                .to("json-validator:schemas/thermometerSchema.json")
                .otherwise()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("{\"error\": \"Unknown device type\"}"))
                .end()
                .unmarshal().json(JsonLibrary.Jackson);

    }
}

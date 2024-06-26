package io.patriotframework.smarthomeplusgateway.Routes;

import io.patriotframework.smarthomeplusgateway.DTO.HouseDTO;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.lang.module.FindException;
import java.net.ConnectException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * HouseRoutes
 * <p>
 * This class is responsible for creating the routes for the houses.
 */
public class HouseRoutes extends RouteBuilder {


    @Override
    public void configure() {
        onException(FindException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error: ${exception.message}")
                .to("direct:handleError")
                .end();

        onException(KeyAlreadyExistsException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error: ${exception.message}")
                .to("direct:handleExistError")
                .end();

        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error: ${exception.message}")
                .to("direct:handleIllegalArgumentException")
                .end();

        onException(ConnectException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error: ${exception.message}")
                .to("direct:invalidHouseAddress")
                .end();

        /*
         * Get house by id from endpoint disabled
         */
        from("direct:getHouseFromEndpoint").disabled()
                .setHeader("HouseAddress", simple("${body.getAddress}"))
                .setBody(simple(""))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD("netty-http:${header.HouseAddress}/api/v0.1/house")
                .unmarshal().json(JsonLibrary.Jackson)
                .choice()
                .when(simple("${header.CamelHttpResponseCode} == 200"))
                .to("direct:validate");


        /*
         * Add house
         */
        from("direct:addHouse")
                .setProperty("house", simple("${body}"))
                .setHeader("HouseAddress", simple("${body[address]}").convertToString())

                .log("House: ${header.HouseAddress}")
                .log("House: ${body}")
                .setBody(simple(""))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.AGGREGATED_TIMEOUT, constant(200))
                .log("before netty")
                .toD("netty-http:${header.HouseAddress}/api/v0.1/house/status?connectTimeout=500&requestTimeout=500&throwExceptionOnFailure=false")
                .log("after netty")
                .log("House: ${body}")
                .choice()
                .when(simple("${body} == \"OK\""))
                .setBody(simple("${exchangeProperty.house}"))
                .marshal().json()
                .to("json-validator:houseschema.json")
                .unmarshal(new JacksonDataFormat(HouseDTO.class))
                .to("bean:HouseService?method=createHouse(${body})")
                .otherwise()
                .throwException(new ConnectException("House is not reachable"))
                .end();

        /*
         * Get house by id
         */
        from("direct:getHouseById")
                .log(LoggingLevel.INFO, "GET /api/house/${header.house}")
                .to("bean:HouseService?method=getHouse(${header.house})")
                .log(LoggingLevel.INFO, "House found: ${body}")
                .end();


        /*
         * Update house
         */
        from("direct:updateHouse")
                .marshal().json()
                .to("json-validator:houseschema.json")
                .log(LoggingLevel.INFO, "PUT /api/house ${body}")
                .unmarshal(new JacksonDataFormat(HouseDTO.class))
                .to("bean:HouseService?method=updateHouse(${body},${header.house})");

        /*
         * Delete house
         */
        from("direct:deleteHouse")
                .log(LoggingLevel.INFO, "DELETE /api/house/${header.house}")
                .to("bean:HouseService?method=deleteHouse(${header.house})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204));

        /*
         * Get all houses
         */
        from("direct:getHouses")
                .to("bean:HouseService?method=getAllHouses");

        from("direct:handleError")
                .log(LoggingLevel.ERROR, "Error: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(exchange -> {
                    Map<String, Object> errorDetails = new HashMap<>();
                    errorDetails.put("status", 404);
                    errorDetails.put("error", "House not Found");
                    errorDetails.put("message", "The requested House was not found.");
                    errorDetails.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
                    errorDetails.put("path", exchange.getIn().getHeader(Exchange.HTTP_URI));
                    errorDetails.put("suggestions", Arrays.asList(
                            "Check if the House ID is correct."
                    ));
                    exchange.getIn().setBody(errorDetails);
                })
                .end();

        from("direct:handleExistError")
                .log(LoggingLevel.ERROR, "Error: ${exception.message}")
                .log("handleExistError")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(409))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(exchange -> {
                    Map<String, Object> errorDetails = new HashMap<>();
                    errorDetails.put("status", 409);
                    errorDetails.put("error", "House already exists");
                    errorDetails.put("message", "The requested House already exists.");
                    errorDetails.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
                    errorDetails.put("path", exchange.getIn().getHeader(Exchange.HTTP_URI));
                    errorDetails.put("suggestions", Arrays.asList(
                            "Check if the House ID is correct."
                    ));
                    exchange.getIn().setBody(errorDetails);
                })
                .end();
        /*
         * Handle IllegalArgumentException
         */
        from("direct:handleIllegalArgumentException")
                .log(LoggingLevel.ERROR, "Error: ${exception.message}")
                .log("handleILLegalArgumentException")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(exchange -> {
                    Map<String, Object> errorDetails = new HashMap<>();
                    errorDetails.put("status", 400);
                    errorDetails.put("error", "Bad Request");
                    errorDetails.put("message", "The request could not be understood by the server due to malformed syntax.");
                    errorDetails.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
                    errorDetails.put("path", exchange.getIn().getHeader(Exchange.HTTP_URI));
                    errorDetails.put("suggestions", Arrays.asList(
                            "Check if the House ID is correct."
                    ));
                    exchange.getIn().setBody(errorDetails);
                })
                .end();
        /*
         * Handle invalid house address
         */
        from("direct:invalidHouseAddress")
                .log(LoggingLevel.ERROR, "Error: ${exception.message}")
                .log("invalidHouseAddress")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(exchange -> {
                    Map<String, Object> errorDetails = new HashMap<>();
                    errorDetails.put("status", 400);
                    errorDetails.put("error", "Bad Request");
                    errorDetails.put("message", "The request could not be understood by the server due to invalid address.");
                    errorDetails.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
                    errorDetails.put("path", exchange.getIn().getHeader(Exchange.HTTP_URI));
                    errorDetails.put("suggestions", Arrays.asList(
                            "Check if the House address is correct."
                    ));
                    exchange.getIn().setBody(errorDetails);
                })
                .end();
    }
}

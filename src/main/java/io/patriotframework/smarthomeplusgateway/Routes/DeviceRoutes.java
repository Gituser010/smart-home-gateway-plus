package io.patriotframework.smarthomeplusgateway.Routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.http.MediaType;

/**
 * DeviceRoutes
 * <p>
 * This class is responsible for creating the routes for the devices.
 */
public class DeviceRoutes extends RouteBuilder {

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .setBody(simple("{\"error\": \"${exception.message}}\"}"))
                .log(LoggingLevel.ERROR, "Error occurred: ${exception.stacktrace}");
        /*
         * Get device by id
         */
        from("direct:getDeviceById").id("getDeviceById")
                .to("bean:HouseService?method=getHouse(${header.house})").id("HouseService")
                .setHeader("HouseAddress", simple("${body.getAddress}"))
                .setBody(simple(""))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .log(LoggingLevel.INFO, "Fetching device with id: ${header.id}")
                .toD("netty-http:${header.HouseAddress}/api/v0.1/house/device/${header.id}?throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Jackson)
                .choice()
                .when(simple("${header.CamelHttpResponseCode} == 200"))
                .to("direct:validate");
        /*
         * Get all devices
         */
        from("direct:getAllDevices")
                .to("bean:HouseService?method=getHouse(${header.house})")
                .setHeader("HouseAddress", simple("${body.getAddress}"))
                .setBody(simple(""))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD("netty-http:${header.HouseAddress}/api/v0.1/house/device?throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Jackson);
        /*
         * Get concrete device
         */
        from("direct:getConcreteDevice")
                .to("bean:HouseService?method=getHouse(${header.house})")
                .setHeader("HouseAddress", simple("${body.getAddress}"))
                .setBody(simple(""))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD("netty-http:${header.HouseAddress}/api/v0.1/house/device/${header.deviceType}/${header.id}?throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Jackson)
                .choice()
                .when(simple("${header.CamelHttpResponseCode} == 200"))
                .to("direct:validate");
        /*
         * Add device
         */
        from("direct:addDevice").disabled()
                .setHeader("DeviceDTO", body())
                .to("bean:HouseService?method=getHouse(${header.house})")
                .setHeader("HouseAddress", simple("${body.getAddress}"))
                .setBody(simple("${header.DeviceDTO}"))
                .to("direct:validate")
                .marshal().json()
                .toD("netty-http:${header.HouseAddress}/api/v0.1/house/device/${header.deviceType}?throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Jackson)
                .choice()
                .when(simple("${header.CamelHttpResponseCode} == 200"))
                .to("direct:validate");
        /*
         * Update device
         */
        from("direct:updateDevice")
                .setProperty("DevID", simple("${header.id}"))
                .setProperty("deviceType", simple("${header.deviceType}"))
                .setProperty("DeviceDTO", body())
                //find house and get house url
                .to("bean:HouseService?method=getHouse(${header.house})")
                .setProperty("HouseAddress", simple("${body.getAddress}"))
                //check correct json format of device
                .setBody(simple("${exchangeProperty.DeviceDTO}"))
                .to("direct:validate")
                //get device from house
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setBody(simple(""))
                .toD("netty-http:${exchangeProperty.HouseAddress}/api/v0.1/house/device/${exchangeProperty.deviceType}/${exchangeProperty.DevID}?throwExceptionOnFailure=false")
                .choice()
                //if device exists
                .when(simple("${header.CamelHttpResponseCode} == 200"))
                //update device using put method
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .setBody(simple("${exchangeProperty.DeviceDTO}"))
                .marshal().json()
                .toD("netty-http:${exchangeProperty.HouseAddress}/api/v0.1/house/device/${exchangeProperty.deviceType}/${exchangeProperty.DevID}?throwExceptionOnFailure=false")
                .end()
                .log(LoggingLevel.INFO, "Body ${body}")
                .choice()
                //if device was successfully updated
                .when(simple("${header.CamelHttpResponseCode} == 200"))
                .unmarshal().json(JsonLibrary.Jackson)
                .to("direct:validate")
                .otherwise()
                .unmarshal().json(JsonLibrary.Jackson);
        /*
         * Delete device
         */
        from("direct:deleteDevice")
                .to("bean:HouseService?method=getHouse(${header.house})")
                .setHeader("HouseAddress", simple("${body.getAddress}"))
                .setBody(simple(""))
                .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                .toD("netty-http:${header.HouseAddress}/api/v0.1/house/device/${header.deviceType}/${header.id}?throwExceptionOnFailure=false")
                .choice()
                .when(simple("${header.CamelHttpResponseCode} == 404"))
                .unmarshal().json(JsonLibrary.Jackson);
    }
}

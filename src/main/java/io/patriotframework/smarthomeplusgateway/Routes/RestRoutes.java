package io.patriotframework.smarthomeplusgateway.Routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;

import static io.patriotframework.smarthomeplusgateway.API.APIRoutes.*;

/**
 * RestRoutes
 * <p>
 * This class is responsible for creating the REST routes for the gateway.
 */
public class RestRoutes extends RouteBuilder {

    @Override
    public void configure() {

        /*
          House routes
         */
        rest()
                /*
                 * Get all houses
                 */
                .get(HOUSE_ROUTE)
                .to("direct:getHouses")
                /*
                 * Add house
                 */
                .post(HOUSE_ROUTE)
                .to("direct:addHouse")
                /*
                 * Get house by id
                 */
                .get(HOUSE_ROUTE_BY_ID)
                .param().name("house").type(RestParamType.path).description("House id").dataType("string").endParam()
                .to("direct:getHouseById")
                /*
                 * Update house
                 */
                .put(HOUSE_ROUTE_BY_ID)
                .to("direct:updateHouse")
                /*
                 * Delete house
                 */
                .delete(HOUSE_ROUTE_BY_ID)
                .param().name("house").type(RestParamType.path).description("House id").dataType("string").endParam()
                .to("direct:deleteHouse");

        /*
         * Device routes
         */
        rest()
                /*
                 * Get device by id
                 */
                .get(DEVICE_ROUTE_BY_ID).description("Get device by id")
                .param().name("house").type(RestParamType.path).description("House name").dataType("string").endParam()
                .param().name("id").type(RestParamType.path).description("Device id").dataType("string").endParam()
                .to("direct:getDeviceById")
                /*
                 * Get all devices
                 */
                .get(DEVICE_ROUTE)
                .param().name("house").type(RestParamType.path).description("House name").dataType("string").endParam()
                .to("direct:getAllDevices")
                /*
                 * Get concrete device
                 */
                .get(CONCRETE_DEVICE_ROUTE)
                .param().name("house").type(RestParamType.path).description("House name").dataType("string").endParam()
                .param().name("deviceType").type(RestParamType.path).description("Device type").dataType("string").endParam()
                .to("direct:getConcreteDevice")
                /*
                 * Add device
                 */
                .post(CONCRETE_DEVICE_ROUTE).disabled()
                .param().name("house").type(RestParamType.path).description("House name").dataType("string").endParam()
                .param().name("deviceType").type(RestParamType.path).description("Device type").dataType("string").endParam()
                .to("direct:addDevice")
                /*
                 * Update device
                 */
                .put(CONCRETE_DEVICE_ROUTE)
                .param().name("house").type(RestParamType.path).description("House name").dataType("string").endParam()
                .param().name("id").type(RestParamType.path).description("Device id").dataType("string").endParam()
                .to("direct:updateDevice")
                /*
                 * Delete device
                 */
                .delete(CONCRETE_DEVICE_ROUTE).disabled()
                .param().name("house").type(RestParamType.path).description("House name").dataType("string").endParam()
                .param().name("id").type(RestParamType.path).description("Device id").dataType("string").endParam()
                .to("direct:deleteDevice");
    }
}

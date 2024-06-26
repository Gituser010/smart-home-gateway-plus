package io.patriotframework.smarthomeplusgateway.API;

/**
 * Class representing all api routes
 */
public class APIRoutes {

    public static final String API_VERSION = "v0.1";
    /**
     * Beginning of the all api routes. All api endpoints share this prefix.
     */
    public static final String API_ROUTE = "/api/v0.1";
    /**
     * Route of the temperature.
     */
    public static final String TEMPERATURE_ROUTE = API_ROUTE + "/temperature";
    /**
     * Route of the humidity.
     */
    public static final String GATEWAY_ROUTE = API_ROUTE + "/gateway";
    /**
     * Route of the house.
     */
    public static final String HOUSE_ROUTE = GATEWAY_ROUTE + "/house";
    /**
     * Route of the house by id.
     */
    public static final String HOUSE_ROUTE_BY_ID = GATEWAY_ROUTE + "/{house}";
    /**
     * Route of the lock.
     */
    public static final String LOCK_ROUTE = HOUSE_ROUTE_BY_ID + "/lock/{label}";
    /**
     * Route of the light.
     */
    public static final String LIGHT_ROUTE = HOUSE_ROUTE_BY_ID + "/light/{label}";
    /**
     * Route of the dimmer light.
     */
    public static final String DIMMER_LIGHT_ROUTE = HOUSE_ROUTE_BY_ID + "/dimerLight/{label}";
    /**
     * Route of the rgb light.
     */
    public static final String RGB_LIGHT_ROUTE = HOUSE_ROUTE_BY_ID + "/rgbLight/{label}";
    /**
     * Route of the fireplace.
     */
    public static final String DOOR_ROUTE = HOUSE_ROUTE_BY_ID + "/door/{label}";
    /**
     * Route of the thermometer.
     */
    public static final String THERMOMETER_ROUTE = HOUSE_ROUTE_BY_ID + "/thermometer/{label}";
    /**
     * Route of the device.
     */
    public static final String DEVICE_ROUTE = HOUSE_ROUTE_BY_ID + "/device";
    /**
     * Route of the device by id.
     */
    public static final String DEVICE_ROUTE_BY_ID = HOUSE_ROUTE_BY_ID + "/device/{id}";
    /**
     * Route of the concrete device.
     */
    public static final String CONCRETE_DEVICE_ROUTE = HOUSE_ROUTE_BY_ID + "/device/{deviceType}/{id}";
    /**
     * Route of the devices.
     */
    public static final String DEVICES_ROUTE = HOUSE_ROUTE_BY_ID + "/devices";

}

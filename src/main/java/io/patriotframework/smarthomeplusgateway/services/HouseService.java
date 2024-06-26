package io.patriotframework.smarthomeplusgateway.services;

import io.patriotframework.smarthomeplusgateway.DTO.HouseDTO;

import java.io.IOException;
import java.util.List;

/**
 * Service for managing houses
 * This service provides methods for creating, updating, deleting and getting houses
 */
public interface HouseService {
    /**
     * Adds a house to the map of houses.
     *
     * @param houseDTO a HouseDTO object representing the house to be added
     */
    void createHouse(HouseDTO houseDTO) throws IOException;

    /**
     * Updates a house in the map of houses.
     *
     * @param houseDTO a HouseDTO object representing the house to be updated with new information
     */
    void updateHouse(HouseDTO houseDTO, String name) throws IllegalArgumentException;

    /**
     * Deletes a house from the map of houses.
     *
     * @param name the name of the house to be deleted
     */
    void deleteHouse(String name);

    /**
     * Gets a house from the map of houses.
     *
     * @param name the name of the house to be retrieved
     * @return a HouseDTO object representing the house
     */
    HouseDTO getHouse(String name);

    /**
     * Gets all houses from the map of houses.
     *
     * @return a list of HouseDTO objects representing all houses
     */
    List<HouseDTO> getAllHouses();
}

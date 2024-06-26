package io.patriotframework.smarthomeplusgateway.house;

import io.patriotframework.smarthomeplusgateway.DTO.HouseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing a house
 */
@Getter
@Setter
@NoArgsConstructor
public class House {
    /**
     * Name of the house
     */
    private String name;
    /**
     * Http address of the house
     */
    private String address;

    /**
     * Constructor
     *
     * @param name    of the house
     * @param address http address of the house
     */
    public House(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Update house address
     *
     * @param houseDTO with new http address
     * @throws IllegalArgumentException if house name is null or empty
     */
    public void update(HouseDTO houseDTO) throws IllegalArgumentException {
        if (houseDTO.getName() != null && !houseDTO.getName().isEmpty()) {
            this.address = houseDTO.getAddress();
        } else {
            throw new IllegalArgumentException("House name can't be null or empty");
        }
    }
}

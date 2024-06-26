import io.patriotframework.smarthomeplusgateway.DTO.HouseDTO;
import io.patriotframework.smarthomeplusgateway.services.HouseService;
import io.patriotframework.smarthomeplusgateway.services.HouseServiceImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.module.FindException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HouseServiceTest {

    @Test
    public void testCreateHouse() throws IOException {
        HouseService houseService = new HouseServiceImpl();
        HouseDTO houseDTO = new HouseDTO();
        houseDTO.setName("house1");
        houseDTO.setAddress("localhost:8080");
        houseDTO.setAddress("localhost:8080");
        houseService.createHouse(houseDTO);
        assertEquals(houseDTO, houseService.getHouse("house1"));
    }

    @Test
    public void testUpdateHouse() throws IOException {
        HouseService houseService = new HouseServiceImpl();
        HouseDTO houseDTO = new HouseDTO();
        houseDTO.setName("house1");
        houseDTO.setAddress("localhost:8080");
        houseService.createHouse(houseDTO);
        houseDTO.setName("house2");
        houseDTO.setAddress("localhost:8080");
        houseService.updateHouse(houseDTO, "house1");
        assertEquals(houseDTO, houseService.getHouse("house2"));
    }

    @Test
    public void testDeleteHouse() throws IOException {
        HouseService houseService = new HouseServiceImpl();
        HouseDTO houseDTO = new HouseDTO();
        houseDTO.setName("house1");
        houseDTO.setAddress("localhost:8080");
        houseService.createHouse(houseDTO);
        houseService.deleteHouse("house1");
        assertThrows(FindException.class, () -> houseService.getHouse("house1"));
    }

    @Test
    public void testGetAllHouses() throws IOException {
        HouseService houseService = new HouseServiceImpl();
        HouseDTO houseDTO1 = new HouseDTO();
        houseDTO1.setName("house1");
        houseDTO1.setAddress("localhost:8080");
        HouseDTO houseDTO2 = new HouseDTO();
        houseDTO2.setName("house2");
        houseDTO2.setAddress("localhost:8081");
        houseService.createHouse(houseDTO1);
        houseService.createHouse(houseDTO2);
        assertEquals(2, houseService.getAllHouses().size());
    }

}

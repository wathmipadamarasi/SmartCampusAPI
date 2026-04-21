package com.smartcampus.resource;

import com.smartcampus.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;


 //Part 3: Sensor Operations & Linking
 // Manages the /api/v1/sensors path.
 
 //Also acts as sub-resource locator for /api/v1/sensors/{sensorId}/readings (Part 4).
 
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    
     // Part 3.2 - GET /api/v1/sensors  (with optional ?type= filter)
     // Returns all sensors, or filters by type if the 'type' query param is provided.
     
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        Collection<Sensor> all = store.getSensors().values();

        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = all.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }
        return Response.ok(all).build();
    }

    
     // Part 3.1 - POST /api/v1/sensors
     // Registers a new sensor. Validates that the referenced roomId actually exists.
     //Throws LinkedResourceNotFoundException -> 422 (Part 5.2).
     
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody("Sensor ID is required.")).build();
        }
        if (store.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody("Sensor ID '" + sensor.getId() + "' already exists.")).build();
        }

        // Validate that the linked room exists
        if (sensor.getRoomId() == null || !store.getRooms().containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Cannot register sensor: Room with ID '" + sensor.getRoomId() + "' does not exist."
            );
        }

        // Default status to ACTIVE if not provided
        if (sensor.getStatus() == null || sensor.getStatus().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        store.getSensors().put(sensor.getId(), sensor);
        store.getSensorReadings().put(sensor.getId(), new ArrayList<>());

        // Update the room's sensorIds list
        Room room = store.getRooms().get(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

   
     // GET /api/v1/sensors/{sensorId}
     // Fetches a specific sensor by ID.
     
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Sensor '" + sensorId + "' not found.")).build();
        }
        return Response.ok(sensor).build();
    }

    
     // Part 4.1 - Sub-Resource Locator
     //Delegates all /api/v1/sensors/{sensorId}/readings requests to SensorReadingResource.
     //JAX-RS will inject the sensorId into the returned resource instance.
     
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }

    private Map<String, String> errorBody(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

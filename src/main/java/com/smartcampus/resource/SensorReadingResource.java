package com.smartcampus.resource;

import com.smartcampus.DataStore;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Part 4: Sub-Resource for Sensor Readings
 * Handles /api/v1/sensors/{sensorId}/readings
 *
 * This class is NOT registered directly as a JAX-RS root resource.
 * It is instantiated via the Sub-Resource Locator in SensorResource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    
     //Part 4.2 - GET /api/v1/sensors/{sensorId}/readings
     //Returns all historical readings for the given sensor.
    @GET
    public Response getReadings() {
        // Validate sensor exists
        if (!store.getSensors().containsKey(sensorId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Sensor '" + sensorId + "' not found.")).build();
        }
        List<SensorReading> readings = store.getSensorReadings()
                .getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    
     //Part 4.2 - POST /api/v1/sensors/{sensorId}/readings
     //Appends a new reading. Throws SensorUnavailableException if sensor is in MAINTENANCE.
     
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensors().get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Sensor '" + sensorId + "' not found.")).build();
        }

        // Part 5.3 - Block readings for sensors in MAINTENANCE status
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is currently under MAINTENANCE and cannot accept new readings."
            );
        }

        // Build the reading with auto-generated ID and timestamp
        SensorReading newReading = new SensorReading(reading.getValue());

        // Persist reading
        store.getSensorReadings()
                .computeIfAbsent(sensorId, k -> new ArrayList<>())
                .add(newReading);

        // SIDE EFFECT: Update the sensor's current value
        sensor.setCurrentValue(newReading.getValue());

        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }

    private Map<String, String> errorBody(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


 //Part 1- Task 2: Discovery Endpoint
 // GET /api/v1 - Returns API metadata, version info, and resource links (HATEOAS).
 
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> discovery = new HashMap<>();

        // API metadata
        discovery.put("api", "Smart Campus Sensor & Room Management API");
        discovery.put("version", "1.0");
        discovery.put("description", "A RESTful API for managing campus rooms and IoT sensors.");
        discovery.put("contact", "admin@smartcampus.ac.uk");

        // HATEOAS - hypermedia links to primary resource collections
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        discovery.put("resources", links);

        return Response.ok(discovery).build();
    }
}

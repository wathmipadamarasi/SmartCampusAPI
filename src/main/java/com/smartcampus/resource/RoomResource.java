package com.smartcampus.resource;

import com.smartcampus.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


 //Part 2:Room Management
 //Handles all CRUD operations for the /api/v1/rooms path.
 
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    
     // Part 2.1 - GET /api/v1/rooms
     // Returns a list of all rooms with their full details.
     
    @GET
    public Response getAllRooms() {
        Collection<Room> rooms = store.getRooms().values();
        return Response.ok(rooms).build();
    }

    
     //Part 2.1 - POST /api/v1/rooms
     //Creates a new room. Returns 201 Created with the new room object.
     
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody("Room ID is required."))
                    .build();
        }
        if (store.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody("A room with ID '" + room.getId() + "' already exists."))
                    .build();
        }
        store.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    
     // Part 2.1 - GET /api/v1/rooms/{roomId}
     //Fetches a specific room by its ID.
     
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Room with ID '" + roomId + "' not found."))
                    .build();
        }
        return Response.ok(room).build();
    }

    
      //Part 2.2 - DELETE /api/v1/rooms/{roomId}
     // Deletes a room. BLOCKED if the room still has sensors assigned to it.
     // Throws RoomNotEmptyException -> mapped to 409 Conflict (Part 5.1).
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Room with ID '" + roomId + "' not found."))
                    .build();
        }
        // Business rule: cannot delete a room that still has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Cannot delete room '" + roomId + "'. It still has " +
                room.getSensorIds().size() + " active sensor(s) assigned to it."
            );
        }
        store.getRooms().remove(roomId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Room '" + roomId + "' successfully deleted.");
        return Response.ok(response).build();
    }

    // Helper to build consistent error response bodies
    private Map<String, String> errorBody(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

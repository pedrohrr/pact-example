package org.pedrohrr.pacts.provider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/messages")
public class MessageResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello() {
        return new Response("Hello");
    }
}

class Response {
    private final String message;

    Response(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
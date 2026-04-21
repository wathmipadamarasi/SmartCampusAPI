package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;


 //Part 5.5 - API Request & Response Logging Filter.

 

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Intercepts every INCOMING request.
     * Logs the HTTP method and full request URI.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.info(String.format(
            "[REQUEST]  Method: %-7s | URI: %s",
            requestContext.getMethod(),
            requestContext.getUriInfo().getRequestUri().toString()
        ));
    }

    
     //Intercepts every OUTGOING response.
     //Logs the HTTP status code returned to the client.
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOG.info(String.format(
            "[RESPONSE] Status: %d | URI: %s",
            responseContext.getStatus(),
            requestContext.getUriInfo().getRequestUri().toString()
        ));
    }
}

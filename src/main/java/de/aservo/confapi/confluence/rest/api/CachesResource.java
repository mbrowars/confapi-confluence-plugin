package de.aservo.confapi.confluence.rest.api;

import de.aservo.confapi.confluence.model.CacheBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface CachesResource {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = {"Cache"},
            summary = "Read all cache informations",
            description = "Returns all information for current cache configuration",
            responses = {
                    @ApiResponse(responseCode = "201", description = "ok", content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CacheBean.class)))}
                    ),
                    @ApiResponse(responseCode = "401", description = "No admin user")
            }
    )
    Response getCaches();

    @GET
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = {"Cache"},
            summary = "Read cache information for a specified cache",
            description = "Returns configuration for a given cache",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ok", content = @Content(schema = @Schema(implementation = CacheBean.class))),
                    @ApiResponse(responseCode = "401", description = "No admin user"),
                    @ApiResponse(responseCode = "404", description = "Cache not found")
            }
    )
    Response getCache(@QueryParam("name") final String name);

    @PUT
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = {"Cache"},
            summary = "Update an existing cache-size",
            description = "Changes the cache size configuration to the specified size",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ok", content = @Content(schema = @Schema(implementation = CacheBean.class))),
                    @ApiResponse(responseCode = "401", description = "No admin user"),
                    @ApiResponse(responseCode = "404", description = "Cache not found"),
                    @ApiResponse(responseCode = "405", description = "Invalid input")
            }
    )
    Response updateCache(@QueryParam("name") final String name, final int size);

    @POST
    @Path("/flush/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = {"Cache"},
            summary = "Flushes a cache",
            description = "Empties the specified cache",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ok", content = @Content(schema = @Schema(implementation = CacheBean.class))),
                    @ApiResponse(responseCode = "401", description = "No admin user"),
                    @ApiResponse(responseCode = "404", description = "Cache not found")
            }
    )
    Response flushCache(@QueryParam("name") final String name);

}

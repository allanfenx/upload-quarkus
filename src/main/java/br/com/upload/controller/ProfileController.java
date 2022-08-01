package br.com.upload.controller;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.MultipartForm;

import br.com.upload.entity.FormData;
import br.com.upload.entity.Profile;
import br.com.upload.service.ProfileService;

@Path("uploads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class ProfileController {

    @Inject
    ProfileService service;

    @GET
    public Response listUploads() {

        List<Profile> profiles = service.listUploads();

        return Response.ok(profiles).build();
    }

    @GET
    @Path("{id}")
    public Response findOne(@PathParam("id") Long id) {

        try {
            Profile profile = service.findOne(id);

            return Response.ok(profile).build();
        } catch (RuntimeException e) {
            return Response.ok(e.getMessage(), MediaType.TEXT_PLAIN).status(404).build();
        }
    }

    @POST
    public Response sendUpload(@MultipartForm FormData data) {

        try {
            Profile profile = service.sendUpload(data);

            return Response.ok(profile).status(201).build();
        } catch (IOException e) {
            return Response.ok(e.getMessage(), MediaType.TEXT_PLAIN).status(401).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response removeUpload(@PathParam("id") Long id) {

        try {
            service.removeUpload(id);

            return Response.status(204).build();
        } catch (IOException e) {
            return Response.ok(e.getMessage(), MediaType.TEXT_PLAIN).status(404).build();
        }
    }
}

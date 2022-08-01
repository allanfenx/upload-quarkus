package br.com.upload.controller;

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
import br.com.upload.service.S3Service;
import br.com.upload.views.ProfileS3View;

@Path("s3")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class S3Controller {

    @Inject
    S3Service service;

    @GET
    public Response listS3() {

        ProfileS3View objects = service.listS3();

        return Response.ok(objects).build();
    }

    @GET
    @Path("{id}")
    public Response findOne(@PathParam("id") Long id) {

        try {
            ProfileS3View profile = service.findOne(id);

            return Response.ok(profile).build();
        } catch (RuntimeException e) {
            return Response.ok(e.getMessage(), MediaType.TEXT_PLAIN).status(404).build();
        }
    }

    @POST
    public Response sendS3(@MultipartForm FormData data) {

        try {
            Profile profile = service.sendS3(data);

            return Response.ok(profile).status(201).build();
        } catch (RuntimeException e) {
            return Response.ok(e.getMessage(), MediaType.TEXT_PLAIN).status(401).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response removeS3(@PathParam("id") Long id) {

        try {
            service.removeS3(id);

            return Response.status(204).build();
        } catch (RuntimeException e) {
            return Response.ok(e.getMessage(), MediaType.TEXT_PLAIN).status(404).build();
        }
    }
}

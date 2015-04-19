package com.prodyna.pac.eternity.server.rest;

import com.prodyna.pac.eternity.server.logging.Logging;
import com.prodyna.pac.eternity.server.model.User;
import com.prodyna.pac.eternity.server.service.UserService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.UUID;

@Stateless
@Logging
@Path("/auth")
public class AuthenticationClientService {

    @Inject
    private Logger logger;

    @Inject
    private UserService userService;

    @GET
    public Response ping() {
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context SecurityContext sc, User user) {

        logger.info("login:\n" + user);

        NewCookie session = new NewCookie("XSRF-TOKEN",UUID.randomUUID().toString(),"/",null,"comment",45,false);

        // TODO verify? create session?
        if (user.getIdentifier().equals("admin")) {

//            public NewCookie(String name, String value, String path, String domain, String comment, int maxAge, boolean secure, boolean httpOnly) {

            return Response.status(Response.Status.UNAUTHORIZED).build();
        }


        logger.info("login:\n" + session);

        return Response.ok().cookie(session).build();

    }

    @DELETE
    public Response logout(@Context Request req) {

        // TODO delete session
        return Response.status(200).build();

    }

//
//    @GET
//    @Path("/hello")
//    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
//    public Response sayHello() {
//
//        List<User> all = userService.findAll();
//
//        return Response.ok(all).build();
//
//    }


//    @Inject
//    HelloService helloService;
//
//    /**
//     * Retrieves a JSON hello world message.
//     * The {@link javax.ws.rs.Path} method annotation value is related to the one defined at the class level.
//     * @return
//     */
//    @GET
//    @Path("json")
//    @Produces({ "application/json" })
//    public JsonObject getHelloWorldJSON() {
//        return Json.createObjectBuilder()
//                .add("result", helloService.createHelloMessage("World"))
//                .build();
//    }
//
//    /**
//     * Retrieves a XML hello world message.
//     * The {@link javax.ws.rs.Path} method annotation value is related to the one defined at the class level.
//     * @return
//     */
//    @GET
//    @Path("xml")
//    @Produces({ "application/xml" })
//    public String getHelloWorldXML() {
//        return "<xml><result>" + helloService.createHelloMessage("World") + "</result></xml>";
//    }
}

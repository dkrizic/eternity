package com.prodyna.pac.eternity.test.authentication.client.impl;

import com.prodyna.pac.eternity.common.helper.impl.RestCookieBuilderImpl;
import com.prodyna.pac.eternity.common.model.authentication.Login;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Client service for authentication.
 */
@Path("/auth")
public interface AuthenticationClientServiceProxy {

    /**
     * Simple test method to check if the client is authenticated.
     *
     * @return status 200 if authentication was successful
     */
    @GET
    Response ping();

    /**
     * Login to the system using a Login object with a username and password.
     *
     * @param login the login container with username and password and the remember flag
     * @return a session cookie and optionally a remember me cookie
     */
    @POST
    @Consumes(RestCookieBuilderImpl.JSON_UTF8)
    @Produces(RestCookieBuilderImpl.JSON_UTF8)
    Response login(Login login);

    /**
     * Login to the system using a rememberMe object.
     *
     * @param sessionCookie    optional old session
     * @param rememberMeCookie the token to login with
     * @param uriInfo          the location of this service
     * @return a session cookie and optionally a remember me cookie
     */
    @GET
    @Produces(RestCookieBuilderImpl.JSON_UTF8)
    @Path("/token")
    Response loginWithToken(@CookieParam(RestCookieBuilderImpl.COOKIE_TOKEN_XSRF) Cookie sessionCookie,
                            @CookieParam(RestCookieBuilderImpl.COOKIE_TOKEN_REMEMBER_ME) Cookie rememberMeCookie,
                            @Context UriInfo uriInfo);

    /**
     * Logs the current user (identified by the xsrf token) out of the system.
     *
     * @param uriInfo          the location of this service
     * @param xsrfCookie       the session
     * @param rememberMeCookie optional cookie, if set and the new connection is not remembered the cookie should
     *                         expire
     * @return 200 if no problem occurred
     */
    @DELETE
    Response logout(@Context UriInfo uriInfo, @CookieParam(RestCookieBuilderImpl.COOKIE_TOKEN_XSRF) Cookie xsrfCookie,
                    @CookieParam(RestCookieBuilderImpl.COOKIE_TOKEN_REMEMBER_ME) Cookie rememberMeCookie);

}

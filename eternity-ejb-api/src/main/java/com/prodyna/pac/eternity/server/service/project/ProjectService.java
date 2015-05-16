package com.prodyna.pac.eternity.server.service.project;

import com.prodyna.pac.eternity.server.exception.functional.ElementAlreadyExistsException;
import com.prodyna.pac.eternity.server.model.FilterRequest;
import com.prodyna.pac.eternity.server.model.FilterResponse;
import com.prodyna.pac.eternity.server.model.booking.Booking;
import com.prodyna.pac.eternity.server.model.project.Project;
import com.prodyna.pac.eternity.server.model.user.User;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Basic service for operating on projects.
 */
@Local
public interface ProjectService {

    /**
     * Creates a new project.
     *
     * @param project the project to create
     * @return the created project with the generated id
     * @throws ElementAlreadyExistsException if a project with the same identifier already exists
     */
    Project create(@NotNull Project project) throws ElementAlreadyExistsException;

    /**
     * Searches for a single project.
     *
     * @param identifier the identifier to search for
     * @return the found project or null if none was found
     */
    Project get(@NotNull String identifier);

    /**
     * Searches for a single project.
     *
     * @param booking the booking the project is mapped to
     * @return the found user
     */
    Project get(@NotNull Booking booking);

    /**
     * Searches for all projects.
     *
     * @return a list of all found projects.
     */
    List<Project> find();

    /**
     * Search for all the bookings of an user.
     *
     * @param filterRequest the filter parameter for this search
     * @return projects which match the filter.
     */
    FilterResponse<Project> find(@NotNull FilterRequest filterRequest);

    /**
     * Updates the given project in the database.
     *
     * @param project the project to be updated
     * @return the updated project
     * @throws ElementAlreadyExistsException if a project with the same identifier already exists
     */
    Project update(@NotNull Project project) throws ElementAlreadyExistsException;

    /**
     * Removes the given project from the database.
     *
     * @param identifier the project to be deleted
     */
    void delete(@NotNull String identifier);

    /**
     * Search for all the projects a user is assigned to.
     *
     * @param user the source user
     * @return projects which are assigned to the user
     */
    List<Project> findAllAssignedToUser(@NotNull User user);

}

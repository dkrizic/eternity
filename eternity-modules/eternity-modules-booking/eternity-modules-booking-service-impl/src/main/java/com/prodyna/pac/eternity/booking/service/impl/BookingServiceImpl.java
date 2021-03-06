package com.prodyna.pac.eternity.booking.service.impl;

import com.prodyna.pac.eternity.booking.service.BookingService;
import com.prodyna.pac.eternity.common.helper.CalendarBuilder;
import com.prodyna.pac.eternity.common.helper.QueryMapBuilder;
import com.prodyna.pac.eternity.common.logging.Logging;
import com.prodyna.pac.eternity.common.model.FilterRequest;
import com.prodyna.pac.eternity.common.model.FilterResponse;
import com.prodyna.pac.eternity.common.model.booking.Booking;
import com.prodyna.pac.eternity.common.model.event.EternityEvent;
import com.prodyna.pac.eternity.common.model.exception.functional.DuplicateTimeBookingException;
import com.prodyna.pac.eternity.common.model.exception.functional.InvalidBookingException;
import com.prodyna.pac.eternity.common.model.exception.functional.UserNotAssignedToProjectException;
import com.prodyna.pac.eternity.common.model.exception.technical.NoSuchElementRuntimeException;
import com.prodyna.pac.eternity.common.model.exception.technical.NotCreatedRuntimeException;
import com.prodyna.pac.eternity.common.model.project.Project;
import com.prodyna.pac.eternity.common.model.user.User;
import com.prodyna.pac.eternity.common.profiling.Profiling;
import com.prodyna.pac.eternity.common.service.CypherService;
import com.prodyna.pac.eternity.project.service.ProjectService;
import com.prodyna.pac.eternity.user.service.UserService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation for the BookingService.
 */
@Logging
@Profiling
@Stateless
public class BookingServiceImpl implements BookingService {

    /**
     * Default return properties, to make object creation easier.
     */
    private static final String BOOKING_RETURN_PROPERTIES =
            "b.id, b.startTime, b.endTime, b.breakDuration, b.description, u.identifier, p.identifier";

    /**
     * Fire events if bookings change to give the ui a chance to react
     */
    @Inject
    private Event<EternityEvent> events;

    @Inject
    private Logger logger;

    @Inject
    private CypherService cypherService;

    @Inject
    private UserService userService;

    @Inject
    private ProjectService projectService;

    @Inject
    private QueryMapBuilder queryMapBuilder;

    @Inject
    private CalendarBuilder calendarBuilder;

    @Override
    public Booking create(@NotNull @Valid final Booking booking, @NotNull @Valid final User user,
                          @NotNull @Valid final Project project)
            throws DuplicateTimeBookingException, UserNotAssignedToProjectException, InvalidBookingException {

        this.checkIfBookingIsValid(booking);

        if (booking.getId() != null) {
            throw new InvalidBookingException("Booking already have an id");
        }

        if (!userService.isAssignedTo(user, project)) {
            throw new UserNotAssignedToProjectException(user.toString());
        }

        booking.setId(UUID.randomUUID().toString());

        this.checkForOverlapping(booking, user);

        final Map<String, Object> queryResult = cypherService.querySingle(
                "MATCH (u:User {id:{1}}), (p:Project {id:{2}}) " +
                        "CREATE (u)<-[:PERFORMED_BY]-" +
                        "(b:Booking {id:{3}, startTime:{4}, endTime:{5}, breakDuration:{6}, description:{7}})-" +
                        "[:PERFORMED_FOR]->(p) " +
                        "RETURN " + BOOKING_RETURN_PROPERTIES,
                queryMapBuilder.map(1, user.getId(), 2, project.getId(), 3, booking.getId(),
                        4, booking.getStartTime().getTimeInMillis(), 5, booking.getEndTime().getTimeInMillis(),
                        6, booking.getBreakDuration(), 7, booking.getDescription()));

        if (queryResult == null) {
            throw new NotCreatedRuntimeException(booking.toString());
        }

        events.fire(EternityEvent.createBookingEvent());

        logger.info("Booking for user {} created", user.getIdentifier());

        return booking;

    }

    @Override
    public Booking get(final String id) {

        Booking result = null;

        final Map<String, Object> queryResult = cypherService.querySingle(
                "MATCH (u:User)<-[:PERFORMED_BY]-(b:Booking {id:{1}})-[:PERFORMED_FOR]->(p:Project) RETURN " +
                        BOOKING_RETURN_PROPERTIES,
                queryMapBuilder.map(1, id));

        if (queryResult != null) {
            result = this.getBooking(queryResult);
        }

        return result;

    }

    @Override
    public List<Booking> findByUser(@NotNull final User user) {

        List<Booking> result = new ArrayList<>();

        final List<Map<String, Object>> queryResult = cypherService.query(
                "MATCH (u:User {id:{1}})<-[:PERFORMED_BY]-(b:Booking)-[:PERFORMED_FOR]->(p:Project) " +
                        "RETURN " + BOOKING_RETURN_PROPERTIES,
                queryMapBuilder.map(1, user.getId()));

        for (Map<String, Object> values : queryResult) {
            result.add(this.getBooking(values));
        }

        return result;

    }

    @Override
    public FilterResponse<Booking> findByUser(@NotNull final User user, @NotNull final FilterRequest filterRequest) {

        filterRequest.setMappings(this.getRequestMappings());
        String filterString = filterRequest.getFilterString();

        int allBookings = (int) cypherService.querySingle(
                "MATCH (u:User {id:{1}})<-[:PERFORMED_BY]-(b:Booking)-[:PERFORMED_FOR]->(p:Project) " +
                        filterString +
                        "RETURN count(b)",
                queryMapBuilder.map(1, user.getId())).get("count(b)");

        filterRequest.setTotalSize(allBookings);

        FilterResponse<Booking> response = new FilterResponse<>();

        response.setTotalSize(allBookings);
        response.setPageSize(filterRequest.getPageSize());
        response.setOffset(filterRequest.getStart());

        if (allBookings > 0) {

            List<Booking> result = new ArrayList<>();

            final List<Map<String, Object>> queryResult = cypherService.query(
                    "MATCH (u:User {id:{1}})<-[:PERFORMED_BY]-(b:Booking)-[:PERFORMED_FOR]->(p:Project) " +
                            filterString +
                            "RETURN " + BOOKING_RETURN_PROPERTIES,
                    queryMapBuilder.map(1, user.getId()), filterRequest);

            for (Map<String, Object> values : queryResult) {
                result.add(this.getBooking(values));
            }

            response.setData(result);

        }

        return response;

    }

    @Override
    public FilterResponse<Booking> find(@NotNull final FilterRequest filterRequest) {

        filterRequest.setMappings(this.getRequestMappings());
        String filterString = filterRequest.getFilterString();

        int allBookings = (int) cypherService.querySingle(
                "MATCH (u:User)<-[:PERFORMED_BY]-(b:Booking)-[:PERFORMED_FOR]->(p:Project) " +
                        filterString +
                        "RETURN count(b)",
                null).get("count(b)");

        filterRequest.setTotalSize(allBookings);

        FilterResponse<Booking> response = new FilterResponse<>();

        response.setTotalSize(allBookings);
        response.setPageSize(filterRequest.getPageSize());
        response.setOffset(filterRequest.getStart());

        if (allBookings > 0) {

            List<Booking> result = new ArrayList<>();

            final List<Map<String, Object>> queryResult = cypherService.query(
                    "MATCH (u:User)<-[:PERFORMED_BY]-(b:Booking)-[:PERFORMED_FOR]->(p:Project) " +
                            filterString +
                            "RETURN " + BOOKING_RETURN_PROPERTIES,
                    null, filterRequest);

            for (Map<String, Object> values : queryResult) {
                result.add(this.getBooking(values));
            }

            response.setData(result);

        }

        return response;

    }

    @Override
    public List<Booking> findByProject(@NotNull final Project project) {

        List<Booking> result = new ArrayList<>();

        final List<Map<String, Object>> queryResult = cypherService.query(
                "MATCH (u:User)<-[:PERFORMED_BY]-(b:Booking)-[:PERFORMED_FOR]->(p:Project {id:{1}}) " +
                        "RETURN " + BOOKING_RETURN_PROPERTIES,
                queryMapBuilder.map(1, project.getId()));

        for (Map<String, Object> values : queryResult) {
            result.add(this.getBooking(values));
        }

        return result;

    }

    @Override
    public List<Booking> findByUserAndProject(@NotNull final User user, @NotNull final Project project) {

        List<Booking> result = new ArrayList<>();

        final List<Map<String, Object>> queryResult = cypherService.query(
                "MATCH (u:User {id:{1}})<-[:PERFORMED_BY]-(b:Booking)-[:PERFORMED_FOR]->(p:Project {id:{2}}) " +
                        "RETURN " + BOOKING_RETURN_PROPERTIES,
                queryMapBuilder.map(1, user.getId(), 2, project.getId()));

        for (Map<String, Object> values : queryResult) {
            result.add(this.getBooking(values));
        }

        return result;

    }

    @Override
    public Booking update(@NotNull @Valid final Booking booking)
            throws DuplicateTimeBookingException, InvalidBookingException {

        this.checkIfBookingIsValid(booking);

        User user = userService.getByBooking(booking);
        Project project = projectService.get(booking);

        this.checkForOverlapping(booking, user);

        final Map<String, Object> queryResult = cypherService.querySingle(
                "MATCH (u:User)<-[:PERFORMED_BY]-(b:Booking {id:{1}})-[:PERFORMED_FOR]->(p:Project) " +
                        "SET b.startTime={2}, b.endTime={3}, b.breakDuration={4}, b.description={5} " +
                        "RETURN " + BOOKING_RETURN_PROPERTIES,
                queryMapBuilder.map(1, booking.getId(), 2, booking.getStartTime().getTimeInMillis(),
                        3, booking.getEndTime().getTimeInMillis(), 4, booking.getBreakDuration(),
                        5, booking.getDescription()));

        if (queryResult == null) {
            throw new NoSuchElementRuntimeException();
        } else {

            events.fire(EternityEvent.createBookingEvent());

            Booking result = this.getBooking(queryResult);
            logger.info("Booking for user {} updated", user.getIdentifier());

            return result;

        }

    }

    @Override
    public void delete(@NotNull final String id) throws NoSuchElementRuntimeException {

        if (this.get(id) == null) {
            throw new NoSuchElementRuntimeException();
        }

        cypherService.query(
                "MATCH ()<-[r1]-(b:Booking {id:{1}})-[r2]->() " +
                        "DELETE r1,b,r2",
                queryMapBuilder.map(1, id));

        events.fire(EternityEvent.createBookingEvent());
        logger.info("Booking wid id {} deleted", id);

    }

    /**
     * Validates if the booking is in the correct format.
     *
     * @param booking the booking to be checked
     * @throws InvalidBookingException if the given booking is inconsistent.
     */
    private void checkIfBookingIsValid(@NotNull final Booking booking) throws InvalidBookingException {

        Calendar startTime = booking.getStartTime();
        Calendar endTime = booking.getEndTime();

        if (startTime == null || endTime == null) {
            throw new InvalidBookingException("Times not set");
        }

        if ((endTime.getTimeInMillis() - startTime.getTimeInMillis()) < 300000) {
            throw new InvalidBookingException("Start has to be at least 5min before end");
        }

        if ((endTime.getTimeInMillis() - startTime.getTimeInMillis()) / 60000 <= booking.getBreakDuration()) {
            throw new InvalidBookingException("Work has to be greater than break");
        }

        boolean timesAtTheSameDay = startTime.get(Calendar.YEAR) == endTime.get(Calendar.YEAR) &&
                startTime.get(Calendar.DAY_OF_YEAR) == endTime.get(Calendar.DAY_OF_YEAR);
        if (!timesAtTheSameDay) {
            throw new InvalidBookingException("Bookings have to be at the same day");
        }

    }

    /**
     * Checks if the booking range overlaps with bookings found for the user
     *
     * @param booking the times to check against
     * @param user    the source user
     * @throws DuplicateTimeBookingException if there is an overlapping
     */
    private void checkForOverlapping(@NotNull final Booking booking, @NotNull final User user)
            throws DuplicateTimeBookingException {

        final List<Map<String, Object>> queryResult = cypherService.query(
                "MATCH (u:User {id:{1}})<-[:PERFORMED_BY]-(b:Booking) " +
                        "WHERE " +
                        "b.id <> {5} AND " +
                        "((b.startTime >= {3} AND b.startTime < {4}) OR" +
                        "(b.endTime > {3} AND b.endTime <= {4}) OR" +
                        "(b.startTime <= {3} AND b.endTime >= {4})) " +
                        "RETURN b.id",
                queryMapBuilder.map(1, user.getId(), 3, booking.getStartTime().getTimeInMillis(),
                        4, booking.getEndTime().getTimeInMillis(), 5, booking.getId()));
        if (queryResult.size() > 0) {
            throw new DuplicateTimeBookingException();
        }

    }

    /**
     * Create valid mappings for the request filter
     *
     * @return created mappings
     */
    private Map<String, String> getRequestMappings() {

        HashMap<String, String> result = new HashMap<>();

        result.put("startTime", "b.startTime");
        result.put("userIdentifier", "u.identifier");
        result.put("projectIdentifier", "p.identifier");
        result.put("description", "b.description");

        return result;

    }

    /**
     * Helper method to construct a Booking from a query response.
     *
     * @param values the available values
     * @return a filled Booking
     */
    private Booking getBooking(final Map<String, Object> values) {

        Booking result = new Booking();

        String readId = (String) values.get("b.id");
        long readStartTime = (long) values.get("b.startTime");
        long readEndTime = (long) values.get("b.endTime");
        int readBreakDuration = (int) values.get("b.breakDuration");
        String readDescription = (String) values.get("b.description");
        String readUserIdentifier = (String) values.get("u.identifier");
        String readProjectIdentifier = (String) values.get("p.identifier");

        result.setId(readId);
        result.setStartTime(calendarBuilder.getCalendar(readStartTime));
        result.setEndTime(calendarBuilder.getCalendar(readEndTime));
        result.setBreakDuration(readBreakDuration);
        result.setDescription(readDescription);
        result.setUserIdentifier(readUserIdentifier);
        result.setProjectIdentifier(readProjectIdentifier);

        return result;

    }

}

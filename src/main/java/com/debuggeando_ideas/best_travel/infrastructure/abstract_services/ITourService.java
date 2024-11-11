package com.debuggeando_ideas.best_travel.infrastructure.abstract_services;

import com.debuggeando_ideas.best_travel.api.models.request.TourRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.TourResponse;

import java.util.UUID;

public interface ITourService extends SimpleCrudService <TourRequest, TourResponse, Long> {

    void removeTicket(Long tourId, UUID ticketId);
    UUID addTicket( Long tourId, Long flyId);

    void removeReservation(Long tourId, UUID reservationId);

    UUID addReservation(Long flyId, Long tourId, Integer totalDays);
}

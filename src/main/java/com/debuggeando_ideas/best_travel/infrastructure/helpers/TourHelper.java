package com.debuggeando_ideas.best_travel.infrastructure.helpers;

import com.debuggeando_ideas.best_travel.domain.entities.jpa.*;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.ReservationRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.TicketRepository;
import com.debuggeando_ideas.best_travel.infrastructure.services.ReservationService;
import com.debuggeando_ideas.best_travel.infrastructure.services.TicketService;
import com.debuggeando_ideas.best_travel.util.BestTravelUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.debuggeando_ideas.best_travel.infrastructure.services.ReservationService.charges_price_percentage;

@Transactional
@Component
@AllArgsConstructor
public class TourHelper {

    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;

    public Set<TicketEntity> createTickets(Set<FlyEntity> flights, CustomerEntity customer) {
        var response = new HashSet<TicketEntity>(flights.size());

        flights.forEach(fly -> {
            var ticketToPersist = TicketEntity.builder()
                    .id(UUID.randomUUID())
                    .fly(fly)
                    .customer(customer)
                    .price(fly.getPrice().add(fly.getPrice().multiply(TicketService.charger_price_percetange)))
                    .purchaseDate(LocalDate.now())
                    .departureDate(BestTravelUtil.getRandomSoon())
                    .arrivalDate(BestTravelUtil.getRandomLatter())
                    .build();

            response.add(this.ticketRepository.save(ticketToPersist));

        });
        return response;
    }

    public Set<ReservationEntity> createReservations(HashMap<HotelEntity, Integer> hotels, CustomerEntity customer) {
        var response = new HashSet<ReservationEntity>(hotels.size());

        hotels.forEach((hotel, totalDays) -> {
            var reservationToPersist = ReservationEntity.builder()
                    .id(UUID.randomUUID())
                    .hotel(hotel)
                    .customer(customer)
                    .totalDays(totalDays)
                    .dateTimeReservation(LocalDateTime.now())
                    .dateStart(LocalDate.now())
                    .dateEnd(LocalDate.now().plusDays(totalDays))
                    .price(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)))
                    .build();

            response.add(this.reservationRepository.save(reservationToPersist));
        });

        return response;
    }

    public TicketEntity createTicket(FlyEntity fly, CustomerEntity customer) {
        var ticketToPersist = TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(fly)
                .customer(customer)
                .price(fly.getPrice().add(fly.getPrice().multiply(TicketService.charger_price_percetange)))
                .purchaseDate(LocalDate.now())
                .departureDate(BestTravelUtil.getRandomSoon())
                .arrivalDate(BestTravelUtil.getRandomLatter())
                .build();

        return this.ticketRepository.save(ticketToPersist);
    }

    public ReservationEntity createReservation(HotelEntity hotel, CustomerEntity customer, Integer totalDays) {
        var reservationToPersist = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .hotel(hotel)
                .customer(customer)
                .totalDays(totalDays)
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(totalDays))
                .price(hotel.getPrice().add(hotel.getPrice().multiply(ReservationService.charges_price_percentage)))
                .build();

        return this.reservationRepository.save(reservationToPersist);
    }
}

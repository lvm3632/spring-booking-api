package com.debuggeando_ideas.best_travel.domain.entities.jpa;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReservationEntity implements Serializable {
    @Id
    private UUID id;
    // date_reservation es un timestamp representa  (YYYY-MM-DDTHH:MM//).
    @Column(name = "date_reservation")
    private LocalDateTime dateTimeReservation;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private Integer totalDays;
    private BigDecimal price;
    // date_start es un date  -> LocalDate representa solo un día

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = true)
    private TourEntity tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;


}

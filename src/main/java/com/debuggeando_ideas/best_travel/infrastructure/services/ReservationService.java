package com.debuggeando_ideas.best_travel.infrastructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.ReservationRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.HotelResponse;
import com.debuggeando_ideas.best_travel.api.models.responses.ReservationResponse;
import com.debuggeando_ideas.best_travel.domain.entities.jpa.ReservationEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.ReservationRepository;
import com.debuggeando_ideas.best_travel.infrastructure.abstract_services.IReservationService;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.ApiCurrencyConnectorHelper;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.BlackListHelper;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.EmailHelper;
import com.debuggeando_ideas.best_travel.util.enums.Tables;
import com.debuggeando_ideas.best_travel.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
@Slf4j
@AllArgsConstructor // 2. Agregar esta anotación de lombok para inicailizar el constructor con los repositorios
public class ReservationService implements IReservationService {

    // 1. Injectamos nuestros servicios primero
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerHelper customerHelper;
    private final BlackListHelper blackListHelper;
    private final ApiCurrencyConnectorHelper currencyConnectorHelper;
    private final EmailHelper emailHelper;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        blackListHelper.isInBlackListCustomer(request.getIdClient());

        var hotel = this.hotelRepository.findById(request.getIdHotel()).orElseThrow(() -> new IdNotFoundException("hotel"));
        var customer = this.customerRepository.findById(request.getIdClient()).orElseThrow(() -> new IdNotFoundException("customer"));

        var reservationToPersist = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .hotel(hotel)
                .customer(customer)
                .totalDays(request.getTotalDays())
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(request.getTotalDays()))
                .price(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)))
                .build();

        var reservationPersisted = reservationRepository.save(reservationToPersist);

        this.customerHelper.increase(customer.getDni(), ReservationService.class);

        if (Objects.nonNull(request.getEmail())) {
            this.emailHelper.sendMail(request.getEmail(), customer.getFullName(), Tables.reservation.name());
        }

        return this.entityToResponse(reservationPersisted);
    }

    @Override
    public ReservationResponse read(UUID uuid) {
        var reservationFromDB = this.reservationRepository.findById(uuid).orElseThrow(() -> new IdNotFoundException("reservation"));
        return this.entityToResponse(reservationFromDB);
    }

    @Override
    public ReservationResponse update(ReservationRequest request, UUID id) {

        var hotel = this.hotelRepository.findById(request.getIdHotel()).orElseThrow(() -> new IdNotFoundException("hotel"));

        var reservationToUpdate = this.reservationRepository.findById(id).orElseThrow(() -> new IdNotFoundException("reservation"));

        reservationToUpdate.setHotel(hotel);
        reservationToUpdate.setTotalDays(request.getTotalDays());
        reservationToUpdate.setDateTimeReservation(LocalDateTime.now());
        reservationToUpdate.setDateStart(LocalDate.now());
        reservationToUpdate.setDateEnd(LocalDate.now().plusDays(request.getTotalDays()));
        reservationToUpdate.setPrice(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)));

        var reservationUpdated = this.reservationRepository.save(reservationToUpdate);

        log.info("Reservation updated with {}", reservationUpdated.getId());

        return this.entityToResponse(reservationUpdated);
    }

    @Override
    public void delete(UUID id) {
        var reservationToDelete = reservationRepository.findById(id).orElseThrow(() -> new IdNotFoundException("reservation"));
        this.reservationRepository.delete(reservationToDelete);
    }

    private ReservationResponse entityToResponse(ReservationEntity entity) {
        var response = new ReservationResponse();
        // La fuente es la entidad y el target response
        // Entonces todos los datos de entity que sean matcheados con response
        BeanUtils.copyProperties(entity, response);
        var hotelResponse = new HotelResponse();
        BeanUtils.copyProperties(entity.getHotel(), hotelResponse);
        response.setHotel(hotelResponse);
        return response;
    }


    @Override
    public BigDecimal findPrice(Long hotelId, Currency currency) {
        var hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        var priceInDollars = hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage));
        if (currency.equals(Currency.getInstance("USD"))) return priceInDollars;
        var currencyDTO = this.currencyConnectorHelper.getCurrency(currency);
        log.info("API currency in {}, response: {}", currencyDTO.getExchangeDate().toString(), currencyDTO.getRates());
        return priceInDollars.multiply(currencyDTO.getRates().get(currency));
    }


    public static final BigDecimal charges_price_percentage = BigDecimal.valueOf(0.20);

}

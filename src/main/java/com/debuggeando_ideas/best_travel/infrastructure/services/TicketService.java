package com.debuggeando_ideas.best_travel.infrastructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.TicketRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.FlyResponse;
import com.debuggeando_ideas.best_travel.api.models.responses.TicketResponse;
import com.debuggeando_ideas.best_travel.domain.entities.jpa.TicketEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.FlyRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.TicketRepository;
import com.debuggeando_ideas.best_travel.infrastructure.abstract_services.ITicketService;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.ApiCurrencyConnectorHelper;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.BlackListHelper;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.EmailHelper;
import com.debuggeando_ideas.best_travel.util.BestTravelUtil;
import com.debuggeando_ideas.best_travel.util.enums.Tables;
import com.debuggeando_ideas.best_travel.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;
// Commit or rollback con transactional
@Transactional // Ver si es spring
@Service
@Slf4j
@AllArgsConstructor
public class TicketService implements ITicketService {

    // Buscar el vuelo y cliente para settearlo en ticket
    //@Autowired // ya está deprecado se inyecta via constructor
    private final FlyRepository flyRepository;
    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;
    private final CustomerHelper customerHelper;
    private final BlackListHelper blackListHelper;
    // Tambien podemos usar el constructor para inyectar los contructores
    private final ApiCurrencyConnectorHelper currencyConnectorHelper;
    private final EmailHelper emailHelper;

    // Y la otra forma es usar lombok
    @Override
    public TicketResponse create(TicketRequest request) {
        blackListHelper.isInBlackListCustomer(request.getIdClient());

        System.out.println("create method");
        var fly  = flyRepository.findById(request.getIdFly()).orElseThrow();
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow();

        var ticketToPersist = TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(fly)
                .customer(customer)
                .price(fly.getPrice().add(fly.getPrice().multiply(charger_price_percetange)))
                .purchaseDate(LocalDate.now())
                .departureDate(BestTravelUtil.getRandomSoon())
                .arrivalDate(BestTravelUtil.getRandomLatter())
                .build();

        var ticketPersisted = this.ticketRepository.save(ticketToPersist);

        customerHelper.increase(customer.getDni(), TicketService.class);

        if (Objects.nonNull(request.getEmail())) {
            this.emailHelper.sendMail(request.getEmail(), customer.getFullName(), Tables.ticket.name());
        }

        log.info("Ticket saved with id: {}", ticketPersisted.getId());
        return this.entityToResponse(ticketPersisted);
    }

    @Override
    public TicketResponse read(UUID id) {
        var ticketFromDB = this.ticketRepository.findById(id).orElseThrow();
        return this.entityToResponse(ticketFromDB);
    }

    @Override
    public TicketResponse update(TicketRequest request, UUID id) {
        var ticketToUpdate = ticketRepository.findById(id).orElseThrow();
        var fly = flyRepository.findById(request.getIdFly()).orElseThrow();

        ticketToUpdate.setFly(fly);
        ticketToUpdate.setPrice(fly.getPrice().add(fly.getPrice().multiply(charger_price_percetange)));
        ticketToUpdate.setDepartureDate(BestTravelUtil.getRandomSoon());
        ticketToUpdate.setArrivalDate(BestTravelUtil.getRandomLatter());

        // Este metodo igual sirve para actualizar o crear
        // Si ve que tiene un id hace update, sino lo crea
        var ticketUpdated = this.ticketRepository.save(ticketToUpdate);



        log.info("Ticket updated with id: {}", ticketUpdated.getId());

        return this.entityToResponse(ticketUpdated);
    }

    @Override
    public void delete(UUID id) {
        var ticketToDelete = ticketRepository.findById(id).orElseThrow();
        this.ticketRepository.delete(ticketToDelete);
    }



    @Override
    public BigDecimal findPrice(Long flyId, Currency currency) {
        var fly = this.flyRepository.findById(flyId).orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));
        if (currency.equals(Currency.getInstance("USD"))) return fly.getPrice().add(fly.getPrice().multiply(charger_price_percetange));
        var currencyDTO = this.currencyConnectorHelper.getCurrency(currency);
        log.info("API currency in {}, response: {}", currencyDTO.getExchangeDate().toString(), currencyDTO.getRates());
        return fly.getPrice().add(fly.getPrice().multiply(charger_price_percetange)).multiply(currencyDTO.getRates().get(currency));
    }

    private TicketResponse entityToResponse(TicketEntity entity) {
        var response = new TicketResponse();
        // La fuente es la entidad y el target response
        // Entonces todos los datos de entity que sean matcheados con response
        BeanUtils.copyProperties(entity, response);
        var flyResponse = new FlyResponse();
        BeanUtils.copyProperties(entity.getFly(), flyResponse);
        response.setFly(flyResponse);
        return response;
    }

    public static final BigDecimal charger_price_percetange = BigDecimal.valueOf(0.25);
}

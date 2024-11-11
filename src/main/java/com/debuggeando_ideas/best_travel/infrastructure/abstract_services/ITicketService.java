package com.debuggeando_ideas.best_travel.infrastructure.abstract_services;

import com.debuggeando_ideas.best_travel.api.models.request.TicketRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.TicketResponse;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public interface ITicketService extends CrudService<TicketRequest, TicketResponse, UUID> {

    // Metodo creado para definir comprtamientos de business
    // Cuando crud no tiene esa operacion definida
    // Operaciones de logica de negocio
    BigDecimal findPrice(Long flyId, Currency currency);
}

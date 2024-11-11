package com.debuggeando_ideas.best_travel.api.controllers;


import com.debuggeando_ideas.best_travel.api.models.request.TicketRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.ErrorsResponse;
import com.debuggeando_ideas.best_travel.api.models.responses.TicketResponse;
import com.debuggeando_ideas.best_travel.infrastructure.abstract_services.ITicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping(path = "ticket")
@AllArgsConstructor
@Tag(name = "Ticket")
public class TicketController {
    private final ITicketService ticketService;

    @ApiResponse(
            responseCode = "400",
            description = "When does the request have a field invalid we response this.",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))
            }
    )
    @Operation(summary = "Save in system un ticket with the fly passed in a parameter")
    @PostMapping
    public ResponseEntity<TicketResponse> post(@Valid @RequestBody TicketRequest request) {
        System.out.println("entro?");

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());

        return ResponseEntity.ok(ticketService.create(request));
    }

    // Este id, debe llevar el mismo que la variable
    // Sino usar name para especificar
    @GetMapping(path = "{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.read(id));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<TicketResponse> put(@Valid @PathVariable UUID id, @RequestBody TicketRequest request) {
        return ResponseEntity.ok(this.ticketService.update(request, id));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getFlyPrice(
            @RequestParam Long flyId,
            @RequestHeader(required = false) Currency currency) {
        if (Objects.isNull(currency)) currency = Currency.getInstance("USD");
        return ResponseEntity.ok(Collections.singletonMap("flyPrice", this.ticketService.findPrice(flyId, currency)));
    }

}

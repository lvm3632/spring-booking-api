package com.debuggeando_ideas.best_travel.api.controllers;


import com.debuggeando_ideas.best_travel.api.models.request.ReservationRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.ErrorsResponse;
import com.debuggeando_ideas.best_travel.api.models.responses.ReservationResponse;
import com.debuggeando_ideas.best_travel.infrastructure.abstract_services.IReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping(path = "reservation")
@AllArgsConstructor
@Tag(name = "Reservation")
public class ReservationController {

    private final IReservationService reservationService;

    @ApiResponse(
            responseCode = "400",
            description = "When does the request have a field invalid we response this.",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))
            }
    )
    @PostMapping
    public ResponseEntity<ReservationResponse> post(@Valid  @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.create(request));
    }

    // Este id, debe llevar el mismo que la variable
    // Sino usar name para especificar
    @GetMapping(path = "{id}")
    public ResponseEntity<ReservationResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.read(id));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<ReservationResponse> put(@Valid @PathVariable UUID id, @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(this.reservationService.update(request, id));
    }

    @Operation(summary = "Delete a reservation with of passed id")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Return a reservation price given by a hotel id")
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>>
    getReservationPrice(@RequestParam Long hotelId,
                        @RequestHeader(required = false) Currency currency) {

        if (Objects.isNull(currency)) currency = Currency.getInstance("USD");

        return ResponseEntity.ok(Collections.singletonMap("ticketPrice",
                this.reservationService
                .findPrice(hotelId, currency)));
    }

}

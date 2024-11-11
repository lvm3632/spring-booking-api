package com.debuggeando_ideas.best_travel.infrastructure.services;

import com.debuggeando_ideas.best_travel.api.models.responses.FlyResponse;
import com.debuggeando_ideas.best_travel.domain.entities.jpa.FlyEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.FlyRepository;
import com.debuggeando_ideas.best_travel.infrastructure.abstract_services.IFlyService;
import com.debuggeando_ideas.best_travel.util.constants.CacheConstants;
import com.debuggeando_ideas.best_travel.util.enums.SortType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class FlyService implements IFlyService {

    private final FlyRepository flyRepository;
    private final WebClient webClient;

    public FlyService(FlyRepository flyRepository, @Qualifier(value = "base") WebClient webClient) {
        this.flyRepository = flyRepository;
        this.webClient = webClient;
    }


    // Page es una lista una coleccion de objetos
    @Override
    public Page<FlyResponse> realAll(Integer page, Integer size, SortType sortType) {
        PageRequest pageRequest = null;

        // Java 17 switch case nuevo
        switch (sortType) {
            case NONE -> pageRequest = PageRequest.of(page, size); // Lambdas de Java 13
            case LOWER -> pageRequest = PageRequest.of(page, size, Sort.by(FIELD_BY_SORT).ascending());
            case UPPER -> pageRequest = PageRequest.of(page, size, Sort.by(FIELD_BY_SORT).descending());
        }

        // Necesitamos regresar un fly response
        return this.flyRepository.findAll(pageRequest).map(this::entityToResponse);
    }

    @Override
    @Cacheable(value = CacheConstants.FLY_CACHE_NAME)
    public Set<FlyResponse> readLessPrice(BigDecimal price) {
        return this.flyRepository.selectLessPrice(price)
                .stream()
                .map(this::entityToResponse)
                .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = CacheConstants.FLY_CACHE_NAME)
    public Set<FlyResponse> readBetweenPrices(BigDecimal min, BigDecimal max) {
        return this.flyRepository.selectBetweenPrice(min, max)
                .stream()
                .map(this::entityToResponse)
                .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = CacheConstants.FLY_CACHE_NAME)
    public Set<FlyResponse> readByOriginDestiny(String origen, String destiny) {
        return this.flyRepository.selectOriginDestiny(origen, destiny)
                .stream()
                .map(this::entityToResponse)
                .collect(Collectors.toSet());
    }

    private FlyResponse entityToResponse(FlyEntity entity) {
        FlyResponse response = new FlyResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}

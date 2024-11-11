package com.debuggeando_ideas.best_travel.infrastructure.abstract_services;

//RQ = Request
//RS = Response
//ID = id
public interface CrudService <RQ, RS, ID> {
    // CRUD = Create Read Update Delete

    RS create(RQ request);

    RS read(ID id);

    // Request y id a actualizar
    RS update(RQ request, ID id);

    void delete(ID id);
}

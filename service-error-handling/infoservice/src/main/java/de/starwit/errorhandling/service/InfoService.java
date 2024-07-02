package de.starwit.errorhandling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.starwit.errorhandling.entities.InfoEntity;
import de.starwit.errorhandling.entities.InfoRepositoryMock;

@Service
public class InfoService {

    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null";

    @Autowired
    private InfoRepositoryMock repository;
    
    public InfoEntity findById(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return repository.findById(id).orElseThrow();
    }
}

package de.starwit.errorhandling.entities;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class InfoRepositoryMock {
    
    public Optional<InfoEntity> findById(Long id) {
        InfoEntity i = null;
        if (id == 1) {
            i = new InfoEntity();
            i.setCreationDate(new Date());
            i.setName("test");
            i.setDescription("descrition");
        }
        return Optional.ofNullable(i);
    }
}

package de.starwit.errorhandling.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.starwit.errorhandling.entities.InfoEntity;
import de.starwit.errorhandling.service.InfoService;

@RestController
@RequestMapping(path = "/api/infos")
public class InfoController {

    @Autowired
    private InfoService service;
 
    @GetMapping("/")
    public List<InfoEntity> findAll() {
        List<InfoEntity> entities = new ArrayList<>();
        InfoEntity i = new InfoEntity();
        i.setCreationDate(new Date());
        i.setName("test");
        i.setDescription("descrition");
        entities.add(i);
        entities.add(i);
        return entities;
    }

    @GetMapping("/{id}")
    public InfoEntity findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping("/exc/")
    public InfoEntity exception() {
        throw new IllegalArgumentException();
    }

}

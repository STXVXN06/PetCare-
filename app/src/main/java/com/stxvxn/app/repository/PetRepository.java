package com.stxvxn.app.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.stxvxn.app.model.Pet;

@Repository
public interface PetRepository extends MongoRepository<Pet, String>  {
    
    List<Pet> findBySpecies(String species);
    List<Pet> findByName(String name);
}

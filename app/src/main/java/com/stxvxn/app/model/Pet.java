package com.stxvxn.app.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection  = "pets")  
public class Pet {
    @Id
    private String id;
    private String name;
    private String species;
    private LocalDateTime createdAt;

      // Constructor vacío (requerido por MongoDB)
      public Pet() {
        this.createdAt = LocalDateTime.now();
    }

    public Pet(String name, String species) {
        this.name = name;
        this.species = species;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

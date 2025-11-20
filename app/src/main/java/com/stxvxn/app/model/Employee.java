package com.stxvxn.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Entidad que representa un empleado del sistema.
 */
@Document(collection = "employees")
@Data
public class Employee {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String employeeId; // ID único legible (ej: EMP001)
    
    private String name;
    private String email;
    private EmployeeRole role;
    private String department;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Employee() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Employee(String employeeId, String name, String email, EmployeeRole role) {
        this();
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}


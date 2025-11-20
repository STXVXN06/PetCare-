package com.stxvxn.app.repository;

import com.stxvxn.app.model.Employee;
import com.stxvxn.app.model.EmployeeRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de empleados.
 */
@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    
    /**
     * Busca un empleado por su ID legible (EMP001, etc.).
     * 
     * @param employeeId ID del empleado
     * @return Empleado encontrado
     */
    Optional<Employee> findByEmployeeId(String employeeId);
    
    /**
     * Busca empleados por rol.
     * 
     * @param role Rol a buscar
     * @return Lista de empleados con ese rol
     */
    List<Employee> findByRole(EmployeeRole role);
    
    /**
     * Busca empleados activos.
     * 
     * @param active Estado activo
     * @return Lista de empleados activos
     */
    List<Employee> findByActive(boolean active);
    
    /**
     * Verifica si existe un empleado con el email dado.
     * 
     * @param email Email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);
}


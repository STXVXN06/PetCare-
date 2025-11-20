package com.stxvxn.app.service;

import com.stxvxn.app.dto.request.EmployeeRequest;
import com.stxvxn.app.dto.response.EmployeeResponse;
import com.stxvxn.app.model.EmployeeRole;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de empleados.
 */
public interface EmployeeService {
    
    /**
     * Crea un nuevo empleado.
     * 
     * @param request Datos del empleado
     * @return Empleado creado
     */
    EmployeeResponse createEmployee(EmployeeRequest request);
    
    /**
     * Busca un empleado por ID.
     * 
     * @param id ID del empleado
     * @return Empleado encontrado
     */
    Optional<EmployeeResponse> findById(String id);
    
    /**
     * Busca un empleado por employeeId (EMP001, etc.).
     * 
     * @param employeeId ID legible del empleado
     * @return Empleado encontrado
     */
    Optional<EmployeeResponse> findByEmployeeId(String employeeId);
    
    /**
     * Obtiene todos los empleados.
     * 
     * @return Lista de empleados
     */
    List<EmployeeResponse> findAll();
    
    /**
     * Busca empleados por rol.
     * 
     * @param role Rol a buscar
     * @return Lista de empleados
     */
    List<EmployeeResponse> findByRole(EmployeeRole role);
    
    /**
     * Valida que un empleado existe y está activo.
     * 
     * @param employeeId ID del empleado
     * @return true si es válido
     * @throws com.stxvxn.app.exception.EmployeeNotFoundException Si no existe
     */
    boolean validateEmployee(String employeeId);
}


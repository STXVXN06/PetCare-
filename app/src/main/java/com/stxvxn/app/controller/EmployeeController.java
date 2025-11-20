package com.stxvxn.app.controller;

import com.stxvxn.app.dto.UpdateStatusRequest;
import com.stxvxn.app.dto.request.EmployeeRequest;
import com.stxvxn.app.dto.response.EmployeeResponse;
import com.stxvxn.app.exception.EmployeeNotFoundException;
import com.stxvxn.app.exception.PackageNotFoundException;
import com.stxvxn.app.model.EmployeeRole;
import com.stxvxn.app.service.EmployeeService;
import com.stxvxn.app.service.PackageService;
import com.stxvxn.app.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de empleados.
 * Maneja operaciones CRUD de empleados y actualización de estados de paquetes.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    
    private final EmployeeService employeeService;
    private final PackageService packageService;
    
    @Value("${instance.name:app}")
    private String instanceName;
    
    /**
     * Crear un nuevo empleado.
     * 
     * @param request Datos del empleado a crear
     * @return Respuesta con el empleado creado
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        log.info("POST /api/employees - Creating employee: {}", request.getEmail());
        
        EmployeeResponse response = employeeService.createEmployee(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseBuilder.success(response, instanceName));
    }
    
    /**
     * Obtener un empleado por ID.
     * 
     * @param id ID del empleado
     * @return Respuesta con el empleado encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeById(@PathVariable String id) {
        log.debug("GET /api/employees/{} - Finding employee by ID", id);
        
        EmployeeResponse response = employeeService.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        
        return ResponseEntity.ok(ResponseBuilder.success(response, instanceName));
    }
    
    /**
     * Obtener un empleado por employeeId (EMP001, etc.).
     * 
     * @param employeeId ID legible del empleado
     * @return Respuesta con el empleado encontrado
     */
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeByEmployeeId(
            @PathVariable String employeeId) {
        log.debug("GET /api/employees/employee-id/{} - Finding employee by employeeId", employeeId);
        
        EmployeeResponse response = employeeService.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        
        return ResponseEntity.ok(ResponseBuilder.success(response, instanceName));
    }
    
    /**
     * Obtener todos los empleados.
     * 
     * @return Respuesta con la lista de empleados
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEmployees() {
        log.info("GET /api/employees - Getting all employees");
        
        List<EmployeeResponse> employees = employeeService.findAll();
        Map<String, Object> response = ResponseBuilder.success(employees, instanceName);
        response.put("count", employees.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener empleados por rol.
     * 
     * @param role Rol del empleado
     * @return Respuesta con la lista de empleados filtrados por rol
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<Map<String, Object>> getEmployeesByRole(@PathVariable String role) {
        log.info("GET /api/employees/role/{} - Getting employees by role", role);
        
        EmployeeRole employeeRole = EmployeeRole.valueOf(role.toUpperCase());
        List<EmployeeResponse> employees = employeeService.findByRole(employeeRole);
        
        Map<String, Object> response = ResponseBuilder.success(employees, instanceName);
        response.put("count", employees.size());
        response.put("role", role);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Actualizar el estado de un paquete (para empleados).
     * 
     * @param trackingNumber Número de rastreo del paquete
     * @param request Datos de actualización de estado
     * @return Respuesta con el paquete actualizado
     */
    @PutMapping("/packages/{trackingNumber}/status")
    public ResponseEntity<Map<String, Object>> updatePackageStatus(
            @PathVariable String trackingNumber,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("PUT /api/employees/packages/{}/status - Updating status by employee: {}", 
                 trackingNumber, request.getUpdatedBy());
        
        // Validar que el empleado existe y está activo
        employeeService.validateEmployee(request.getUpdatedBy());
        
        var packageResponse = packageService.updateStatus(trackingNumber, request)
                .orElseThrow(() -> new PackageNotFoundException(trackingNumber));
        
        Map<String, Object> response = ResponseBuilder.success(packageResponse, instanceName);
        response.put("message", "Estado actualizado exitosamente");
        
        return ResponseEntity.ok(response);
    }
}

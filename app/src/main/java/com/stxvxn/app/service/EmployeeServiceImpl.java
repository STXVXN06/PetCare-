package com.stxvxn.app.service;

import com.stxvxn.app.dto.request.EmployeeRequest;
import com.stxvxn.app.dto.response.EmployeeResponse;
import com.stxvxn.app.exception.EmployeeNotFoundException;
import com.stxvxn.app.exception.ValidationException;
import com.stxvxn.app.model.Employee;
import com.stxvxn.app.model.EmployeeRole;
import com.stxvxn.app.repository.EmployeeRepository;
import com.stxvxn.app.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para gestión de empleados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee: {}", request.getEmail());
        
        // Validar email único
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Ya existe un empleado con el email: " + request.getEmail());
        }
        
        // Generar employeeId único
        String employeeId = generateUniqueEmployeeId();
        log.debug("Generated employee ID: {}", employeeId);
        
        // Crear empleado
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());
        employee.setDepartment(request.getDepartment());
        employee.setActive(true);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        
        Employee saved = employeeRepository.save(employee);
        log.info("Employee created successfully with ID: {} and employeeId: {}", 
                 saved.getId(), saved.getEmployeeId());
        
        return new EmployeeResponse(saved);
    }
    
    @Override
    public Optional<EmployeeResponse> findById(String id) {
        log.debug("Finding employee by ID: {}", id);
        return employeeRepository.findById(id)
                .map(EmployeeResponse::new);
    }
    
    @Override
    public Optional<EmployeeResponse> findByEmployeeId(String employeeId) {
        log.debug("Finding employee by employeeId: {}", employeeId);
        return employeeRepository.findByEmployeeId(employeeId)
                .map(EmployeeResponse::new);
    }
    
    @Override
    public List<EmployeeResponse> findAll() {
        log.debug("Finding all employees");
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EmployeeResponse> findByRole(EmployeeRole role) {
        log.debug("Finding employees by role: {}", role);
        return employeeRepository.findByRole(role).stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean validateEmployee(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("El ID del empleado es requerido");
        }
        
        Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
        
        if (employee.isEmpty()) {
            throw new EmployeeNotFoundException(employeeId);
        }
        
        if (!employee.get().isActive()) {
            throw new ValidationException("El empleado no está activo: " + employeeId);
        }
        
        log.debug("Employee validated: {}", employeeId);
        return true;
    }
    
    /**
     * Genera un ID único de empleado en formato EMP001, EMP002, etc.
     * 
     * @return ID único de empleado
     */
    private String generateUniqueEmployeeId() {
        // Buscar el último número usado
        List<Employee> allEmployees = employeeRepository.findAll();
        
        // Si no hay empleados, empezar con EMP001
        if (allEmployees.isEmpty()) {
            return String.format("%s%03d", Constants.EMPLOYEE_PREFIX, 1);
        }
        
        // Encontrar el número más alto usado
        int maxNumber = allEmployees.stream()
                .map(Employee::getEmployeeId)
                .filter(id -> id != null && id.startsWith(Constants.EMPLOYEE_PREFIX))
                .mapToInt(id -> {
                    try {
                        String numberPart = id.substring(Constants.EMPLOYEE_PREFIX.length());
                        return Integer.parseInt(numberPart);
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
        
        // Generar el siguiente número
        int nextNumber = maxNumber + 1;
        return String.format("%s%03d", Constants.EMPLOYEE_PREFIX, nextNumber);
    }
}


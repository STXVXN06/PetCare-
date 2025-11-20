package com.stxvxn.app.dto.response;

import com.stxvxn.app.model.Employee;
import com.stxvxn.app.model.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para empleados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private String id;
    private String employeeId;
    private String name;
    private String email;
    private EmployeeRole role;
    private String roleDisplayName;
    private String department;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Constructor desde entidad Employee.
     * 
     * @param employee Entidad Employee
     */
    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.employeeId = employee.getEmployeeId();
        this.name = employee.getName();
        this.email = employee.getEmail();
        this.role = employee.getRole();
        this.roleDisplayName = employee.getRole().getDisplayName();
        this.department = employee.getDepartment();
        this.active = employee.isActive();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
    }
}


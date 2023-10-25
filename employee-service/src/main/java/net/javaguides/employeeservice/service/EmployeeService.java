package net.javaguides.employeeservice.service;

import net.javaguides.employeeservice.dto.APIResponseDto;
import net.javaguides.employeeservice.dto.EmployeeDto;
import net.javaguides.employeeservice.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

public interface EmployeeService {
    EmployeeDto saveEmployee(EmployeeDto employeeDto);

    ResponseEntity<APIResponseDto> getEmployeeById(Long employeeId) throws ResourceNotFoundException;
}

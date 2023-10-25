package net.javaguides.employeeservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEvent {
    private Long id;
    private String firstName;
    private String lastName;
}

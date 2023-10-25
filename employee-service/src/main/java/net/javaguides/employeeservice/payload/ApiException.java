package net.javaguides.employeeservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ApiException {
    private String message;
    private boolean success;
    private HttpStatus status;
}

package net.javaguides.employeeservice.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import net.javaguides.employeeservice.dto.APIResponseDto;
import net.javaguides.employeeservice.dto.DepartmentDto;
import net.javaguides.employeeservice.dto.EmployeeDto;
import net.javaguides.employeeservice.entity.Employee;
import net.javaguides.employeeservice.event.EmployeeEvent;
import net.javaguides.employeeservice.exception.ResourceNotFoundException;
import net.javaguides.employeeservice.mapper.EmployeeMapper;
import net.javaguides.employeeservice.repository.EmployeeRepository;
import net.javaguides.employeeservice.service.APIClient;
import net.javaguides.employeeservice.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private EmployeeRepository employeeRepository;

    @Autowired
    private KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    //private RestTemplate restTemplate;
    private WebClient webClient;
    private APIClient apiClient;

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {

        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);

        Employee saveDEmployee = employeeRepository.save(employee);

        EmployeeDto savedEmployeeDto = EmployeeMapper.mapToEmployeeDto(saveDEmployee);

        return savedEmployeeDto;
    }

    @Retry(name = "${spring.application.name}",fallbackMethod = "getDefaultDepartment")
    //@CircuitBreaker(name = "${spring.application.name}",fallbackMethod = "getDefaultDepartment")
    @Override
    public ResponseEntity<APIResponseDto> getEmployeeById(Long employeeId) throws ResourceNotFoundException {

        LOGGER.info("Inside getEmployeeId() method");

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(()->new ResourceNotFoundException("User with given ID is not FOUND!!!"));
        kafkaTemplate.send("notificationTopic",new EmployeeEvent(employee.getId(),employee.getFirstName(),employee.getLastName()));
     /*   ResponseEntity<DepartmentDto> responseEntity = restTemplate.getForEntity("http://DEPARTMENT-SERVICE/api/departments/" + employee.getDepartmentCode(),
                DepartmentDto.class);

        DepartmentDto departmentDto = responseEntity.getBody();*/

        DepartmentDto departmentDto = webClient.get()
                .uri("http://localhost:8090/api/departments/" + employee.getDepartmentCode())
                .retrieve()
                .bodyToMono(DepartmentDto.class)
                .block();

        //DepartmentDto departmentDto = apiClient.getDepartment(employee.getDepartmentCode());

        EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(employee);

        APIResponseDto apiResponseDto = new APIResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);

        return ResponseEntity.ok(apiResponseDto);
    }

    public ResponseEntity<APIResponseDto> getDefaultDepartment(Long employeeId, Exception exception) throws ResourceNotFoundException {

        LOGGER.info("Inside getDefaultDepartment() method");
        LOGGER.info("DEPARTMENT-SERVICE IS SHUTDOWN: {} ",exception.getMessage() );
        //Employee employee = employeeRepository.findById(employeeId).get();
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(()->new ResourceNotFoundException("User with given ID is not FOUND!!!"));

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setDepartmentName("R&D Department");
        departmentDto.setDepartmentCode("RD001");
        departmentDto.setDepartmentDescription("Research and Development Department");


        EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(employee);

        APIResponseDto apiResponseDto = new APIResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);

        return new ResponseEntity<>(apiResponseDto, HttpStatus.NOT_FOUND);
    }
}

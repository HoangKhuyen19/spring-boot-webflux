package com.khuyen.springbootwebflux.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.khuyen.springbootwebflux.model.Employee;
import com.khuyen.springbootwebflux.model.EmployeeRepository;
import com.khuyen.springbootwebflux.response.Response;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    // Field
    private final EmployeeRepository employeeRepository;

    // Constructor:
    public EmployeeController() {
        this.employeeRepository = EmployeeRepository.getInstance();
    }

    @GetMapping("/{idStr}")
    public Mono<Response<Employee>> getEmployeeById(@PathVariable String idStr) {
        Response<Employee> response = new Response<>();

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(
                    "ID must be an integer!");
            response.setCode(HttpStatus.BAD_REQUEST.toString());
            return Mono.just(response);
        }

        Employee employee = employeeRepository.get(id);

        if (employee == null) {
            response.setSuccess(false);
            response.setCode(HttpStatus.NOT_FOUND.toString());
            response.setMessage(
                    "Employee not found!");
            response.setCode(HttpStatus.NOT_FOUND.toString());
            return Mono.just(response);
        }

        response.setSuccess(true);
        response.setResult(employee);
        return Mono.just(response);
    }

    @GetMapping
    public Mono<Response<List<Employee>>> getAllEmployee() {
        Response<List<Employee>> response = new Response<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(true);
        response.setResult(employeeRepository.getAll());
        return Mono.just(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> insert(@RequestBody Employee employee) {
        Response<Void> response = new Response<>();

        if(employeeRepository.get(employee.getId()) != null) {
            response.setSuccess(false);
            response.setMessage("Employee with given id already exist!");
            response.setCode("EMPLOYEE_ALREADY_EXIST");
            return Mono.just(response);
        }

        employeeRepository.insert(employee);
        response.setSuccess(true);
        return Mono.just(response);
    }

    @PutMapping("/{idStr}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> update(@PathVariable String idStr, @RequestBody Employee employee) {
        Response<Void> response = new Response<>();

        int id;
        try {
            id = Integer.parseInt(idStr);
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("ID must be an integer!");
            response.setCode("ID_INVALID");
            return Mono.just(response);
        }

        Employee target = employeeRepository.get(id);

        if (target == null) {
            response.setSuccess(false);
            response.setMessage("Employee with with given id doesn't exist!");
            response.setCode("EMPLOYEE_NOT_EXIST");
            return Mono.just(response);
        }

        target.setName(employee.getName());

        response.setSuccess(true);
        return Mono.just(response);
    }

    @DeleteMapping("/generateEmployees")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> delelte(@PathVariable String id) {

        Response<Void> response = new Response<Void>();
        
        try {
            employeeRepository.cancelGenerateEmployees();
        }
        catch (Exception e) {
            e.printStackTrace();

            response.setSuccess(false);
            response.setMessage(
                "Failed while canceling generate employees!"
            );
            response.setCode("FAILED_CANCELING_GENERATE_EMPLOYEES");

            return Mono.just(response);
        }
        
        response.setSuccess(true);

        return Mono.just(response);
    }
}
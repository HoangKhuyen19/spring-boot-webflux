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
import org.springframework.web.bind.annotation.RequestParam;
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
       return employeeRepository.get(idStr);
    }

    @GetMapping
    public Mono<Response<List<Employee>>> getByKeyword(@RequestParam(name="keyword", required = false) String keyword) {
        return (
            keyword == null 
            ? employeeRepository.getAll()
            : employeeRepository.getByKeyword(keyword)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> insert(@RequestBody Employee employee) {
        return employeeRepository.insert(employee);
    }

    @PutMapping("/{idStr}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> update(@PathVariable String idStr, @RequestBody Employee employee) {
        return employeeRepository.update(idStr, employee);
    }

    @DeleteMapping("/generateEmployees")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> cancelGenerateEmployees() {

        return employeeRepository.cancelGenerateEmployees();
    }

    @DeleteMapping("/{idStr}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> delete(@PathVariable String idStr){
        return employeeRepository.delete(idStr);
    }

    @GetMapping("/test")
    public Mono test() {
        
        return Mono.fromSupplier(()->{
            throw new RuntimeException();
        })
        .onErrorResume(
            e -> {
                return Mono.just("Error");
            }
        );
    }
}
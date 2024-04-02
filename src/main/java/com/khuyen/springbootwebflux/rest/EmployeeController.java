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

    // Constructor:
    public EmployeeController() {
    }

    @GetMapping("/{idStr}")
    public Mono<Response<Employee>> getEmployeeById(@PathVariable String idStr) {
        Response<Employee> response = new Response<>();

        try {
            Employee employee = EmployeeRepository
            .getInstance()
            .get(idStr);
            response.setResult(employee);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return Mono.just(response);
    }

    @GetMapping
    public Mono<Response<List<Employee>>> getByKeyword(
            @RequestParam(name = "keyword", required = false) String keyword) {
        Response<List<Employee>> response = new Response<>();

        try {
            response.setResult(
                (
                    keyword == null
                    ? EmployeeRepository.getInstance().getAll()
                    : EmployeeRepository.getInstance().getByKeyword(keyword)
                )
            );
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return Mono.just(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> insert(@RequestBody Employee employee) {
        Response<Void> response = new Response<>();

        try {
            EmployeeRepository.getInstance().insert(employee);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return Mono.just(response);
    }

    @PutMapping("/{idStr}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> update(@PathVariable String idStr, @RequestBody Employee employee) {
        Response<Void> response = new Response<>();

        try {
            EmployeeRepository.getInstance().update(idStr, employee);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return Mono.just(response);
    }

    @DeleteMapping("/generateEmployees")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> cancelGenerateEmployees() {
        Response<Void> response = new Response<>();

        try {
            EmployeeRepository.getInstance().cancelGenerateEmployees();
        }
        catch (Error e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return Mono.just(response);
    }

    @DeleteMapping("/{idStr}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response<Void>> delete(@PathVariable String idStr) {
        Response<Void> response = new Response<>();

        try {
            EmployeeRepository.getInstance().delete(idStr);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return Mono.just(response);
    }
}
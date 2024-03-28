package com.khuyen.springbootwebflux.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.khuyen.springbootwebflux.response.Response;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class EmployeeRepository {
    // Static fields:
    private static final int LIMIT = 10000;
    public static EmployeeRepository instance;

    // Static method:
    public static EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }

        return instance;
    }

    // Fields:
    private List<Employee> employees;
    private Disposable generateEmployeesDisposable;

    // Constructor:
    public EmployeeRepository() {
        employees = new ArrayList<>();

        generateEmployeesDisposable = Flux.range(0, LIMIT)
                .publishOn(Schedulers.parallel())
                .subscribe(
                        id -> {
                            employees.add(
                                    new Employee(id, "Employee " + id));
                            System.out.println("Added employee with ID: " + id);

                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                            }
                        });
    }

    // Methods:
    public Mono<Response<List<Employee>>> getAll() {
        return Mono.fromSupplier(() -> {
            Response<List<Employee>> response = new Response<>();

            response.setSuccess(true);
            response.setResult(employees);
            return response;
        }).publishOn(Schedulers.parallel());
    }

    public Mono<Response<Employee>> get(String idStr) {
        return Mono.fromSupplier(() -> {
            Response<Employee> response = new Response<>();

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (Exception e) {
                response.setSuccess(false);
                response.setMessage(
                        "ID must be an integer!");
                response.setCode(HttpStatus.BAD_REQUEST.toString());
                return response;
            }

            Employee employee = this.getEmployee(id);

            if (employee == null) {
                response.setSuccess(true);
                response.setMessage("Employee not found!");
                response.setCode("EMPLOYEE_NOTFOUND");
                return response;
            }

            response.setSuccess(true);
            response.setResult(employee);
            return response;
        }).publishOn(Schedulers.parallel());
    }

    public Mono<Response<List<Employee>>> getByKeyword(String keyword) {
        return Mono.fromSupplier(() -> {
            Response<List<Employee>> response = new Response<>();

            if (keyword == null) {
                response.setSuccess(false);
                response.setMessage("Key word cannot be null!");
                return response;
            }

            if (keyword.equals("")) {
                response.setSuccess(false);
                response.setMessage("Keyword cannot be empty!");
                return response;
            }

            List<Employee> result = new ArrayList<>();

            for (Employee employee : employees) {
                if (employee.toString().toLowerCase().contains(keyword.toLowerCase())) {
                    result.add(employee);
                }
            }

            response.setSuccess(true);
            response.setResult(result);
            return response;
        }).publishOn(Schedulers.parallel());
    }

    public Mono<Response<Void>> insert(Employee employee) {

        return Mono.fromSupplier(() -> {
            Response<Void> response = new Response<>();

            if (this.getEmployee(employee.getId()) != null) {
                response.setSuccess(false);
                response.setMessage("Employee with given id already exist!");
                response.setCode("EMPLOYEE_ALREADY_EXIST");
                return response;
            }
            employees.add(employee);
            response.setSuccess(true);

            return response;
        });
    }

    public Mono<Response<Void>> update(String idStr, Employee employee) {

        return Mono.fromSupplier(() -> {
            Response<Void> response = new Response<>();

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (Exception e) {
                response.setSuccess(false);
                response.setMessage("ID must be an integer!");
                response.setCode("ID_INVALID");
                return response;
            }

            Employee target = this.getEmployee(id);

            if (target == null) {
                response.setSuccess(false);
                response.setMessage("Employee with given id doesn't exist!");
                response.setCode("EMPLOYEE_NOT_EXIST");
                return response;
            }

            target.setName(employee.getName());
            response.setSuccess(true);
            return response;
        }).publishOn(Schedulers.parallel());
    }

    public Mono<Response<Void>> delete(String idStr) {
        return Mono.fromSupplier(() -> {
            Response<Void> response = new Response<>();

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (Exception e) {
                response.setSuccess(false);
                response.setMessage("ID must be an integer!");
                response.setCode("ID_INVALID");
                return response;
            }

            if (this.getEmployee(id) == null) {
                response.setSuccess(false);
                response.setMessage("Employee with given id doesn't exist!");
                response.setCode("EMPLOYEE_NOT_EXIST");
                return response;
            }

            for (Employee employee : this.employees) {
                if (employee.getId() == id) {
                    employees.remove(employee);
                    break;
                }
            }

            response.setSuccess(true);
            return response;
        });
    }

    public Mono<Response<Void>> cancelGenerateEmployees() {
        return Mono.fromSupplier(() -> {
            Response<Void> response = new Response<Void>();

            try {
                if (!generateEmployeesDisposable.isDisposed()) {
                    generateEmployeesDisposable.dispose();
                }
            } catch (Exception e) {
                e.printStackTrace();

                response.setSuccess(false);
                response.setMessage(
                        "Failed while canceling generate employees!");
                response.setCode("FAILED_CANCELING_GENERATE_EMPLOYEES");

                return response;
            }

            response.setSuccess(true);

            return response;
        });
    }

    private Employee getEmployee(int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }
}

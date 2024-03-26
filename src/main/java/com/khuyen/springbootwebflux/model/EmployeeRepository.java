package com.khuyen.springbootwebflux.model;

import java.util.ArrayList;
import java.util.List;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class EmployeeRepository {
    //Static fields:
    private static final int LIMIT = 10000;
    public static EmployeeRepository instance;

    //Static method:
    public static EmployeeRepository getInstance() {
        if (instance == null) {
            instance =  new EmployeeRepository();
        }

        return instance;
    }

    //Fields:
    private List<Employee> employees;
    private Disposable generateEmployeesDisposable;

    //Constructor:
    public EmployeeRepository() {
        employees = new ArrayList<>();

        generateEmployeesDisposable = Flux.range(0, LIMIT)
        .publishOn(Schedulers.parallel())
        .subscribe(
            id -> {
                employees.add(
                    new Employee(id, "Employee " + id)
                );
                System.out.println("Added employee with ID: " + id);
                
                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {
                }
            }
        );
    }

    //Methods:
    public List<Employee> getAll() {
        return employees;
    }

    public Employee get(int id) {
        for(Employee employee : this.employees) {
            if(employee.getId() == id) {
                return employee;
            }
        }

        return null;
    }

    public void insert(Employee employee) {
        employees.add(employee);
    }

    public void cancelGenerateEmployees() {
        if(!generateEmployeesDisposable.isDisposed()){
            generateEmployeesDisposable.dispose();
        }
    }
}

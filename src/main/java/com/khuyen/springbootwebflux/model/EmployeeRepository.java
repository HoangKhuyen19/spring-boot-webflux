package com.khuyen.springbootwebflux.model;

import java.util.ArrayList;
import java.util.List;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
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
    public List<Employee> getAll() {
        return employees;
    }

    public Employee get(String idStr) {
        int id;

        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            throw new Error("ID must be an Integer!");
        }

        Employee target = null;

        for (Employee employee : employees) {
            if (employee.getId() == id) {
                target = employee;
                break;
            }
        }

        return target;
    }

    public List<Employee> getByKeyword(String keyword) {

        if (keyword == null) {
            throw new Error("Key word cannot be null");
        }

        if (keyword.equals("")) {
            throw new Error("Keyword cannot be empty!");
        }

        List<Employee> result = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.toString().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(employee);
            }
        }

        return result;
    }

    public void insert(Employee employee) {
        for(Employee e : employees) {
            if(e.getId() == employee.getId()) {
                throw new Error("Employee with given ID already exist!");
            }
        }

        employees.add(employee);
    }

    public void update(String idStr, Employee employee) {
        Employee target = this.get(idStr);

        if(target == null) {
            throw new Error("Employee with given ID doesn't exist!");
        }

        target.setName(employee.getName());
    }

    public void delete(String idStr) {
        Employee target = this.get(idStr);

        if (target == null) {
            throw new Error("Employee with given ID doesn't exist!");
        }

        employees.remove(target);
    }

    public void cancelGenerateEmployees() {
        generateEmployeesDisposable.dispose();
    }
}

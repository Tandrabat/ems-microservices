package com.microservices.ems.department_service.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.microservices.ems.department_service.entity.Department;
import com.microservices.ems.department_service.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository repo;

    public DepartmentService(DepartmentRepository repo) {
        this.repo = repo;
    }

    public Department save(Department d) {
        return repo.save(d);
    }

    public List<Department> getAll() {
        return repo.findAll();
    }
}
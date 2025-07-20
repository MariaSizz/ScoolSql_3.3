package ru.hogwarts.school_sql.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school_sql.model.Student;
import ru.hogwarts.school_sql.repository.StudentRepository;

import java.util.List;
import java.util.Optional;


@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student createStudent(Student student) {

        return repository.save(student);
    }

    public Student findStudent(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Student editStudent(Student student) {
        if (repository.existsById(student.getId())) {
            return repository.save(student);
        }
        return null;
    }

    public Student deleteStudent(Long id) {
        final Optional<Student> studentOptional = repository.findById(id);
        if (studentOptional.isPresent()) {
            repository.deleteById(id);
            return studentOptional.get();
        }
        return null;
    }

    public List<Student> findByAge(int age) {
        return repository.findByAge(age);
    }


}

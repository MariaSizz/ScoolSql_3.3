package ru.hogwarts.school_sql.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school_sql.model.Faculty;
import ru.hogwarts.school_sql.model.Student;
import ru.hogwarts.school_sql.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }
    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id){
        Student student = service.findStudent(id);
        if (student == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student){
        Student createdStudent = service.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);

    }
    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student){
        Student editedStudent = service.editStudent(student);
        if (editedStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(editedStudent);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id){
        Student student = service.deleteStudent(id);
        if (student == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Student>> findStudentsByAge(@RequestParam int age){
        List<Student> students = service.findByAge(age);
        return ResponseEntity.ok(students);
    }
    @GetMapping("/age-between")
    public ResponseEntity<List<Student>> findStudentsByAgeBetween(@RequestParam int min, @RequestParam int max){
        final List<Student> students = service.findByAgeBetween(min, max);
        return ResponseEntity.ok(students);
    }
    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id){
        final Faculty studentFaculty = service.getStudentFaculty(id);
        return ResponseEntity.ok(studentFaculty);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalStudentsCount() {
        Integer totalStudentsCount = service.getTotalStudentsCount();
        return ResponseEntity.ok(totalStudentsCount);
    }

    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageStudentAge() {
        Double averageStudentAge = service.getAverageStudentAge();
        return ResponseEntity.ok(averageStudentAge);
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return service.getLastFiveStudents();
    }

}

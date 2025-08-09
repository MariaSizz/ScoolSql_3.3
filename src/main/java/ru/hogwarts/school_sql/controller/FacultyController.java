package ru.hogwarts.school_sql.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school_sql.model.Faculty;
import ru.hogwarts.school_sql.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }
    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id){
        Faculty faculty = service.findFaculty(id);
        if (faculty == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }
    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty){
        Faculty createdFaculty = service.createFaculty(faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);

    }
    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty){
        Faculty editedFaculty = service.editFaculty(faculty);
        if (editedFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(editedFaculty);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id){
        Faculty faculty = service.deleteFaculty(id);
        if (faculty == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Faculty>> findFacultyByColor(@RequestParam String color){
        List<Faculty> faculties = service.findByColor(color);
        return ResponseEntity.ok(faculties);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Faculty>> findFacultiesByNameOrColor(@RequestParam String query){
        final List<Faculty> faculties = service.findByNameOrColor(query);
        return ResponseEntity.ok(faculties);
    }
}

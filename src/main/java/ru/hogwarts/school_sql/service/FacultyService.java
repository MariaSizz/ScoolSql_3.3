package ru.hogwarts.school_sql.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school_sql.model.Faculty;
import ru.hogwarts.school_sql.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {
     private final FacultyRepository repository;
     public FacultyService(FacultyRepository repository){
         this.repository = repository;
     }

    public Faculty createFaculty(Faculty faculty){
       return repository.save(faculty);
    }
    public Faculty findFaculty(Long id){
        return repository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty){
        if (repository.existsById(faculty.getId())){
            return repository.save(faculty);
        }
        return null;
    }
    public Faculty deleteFaculty(Long id){
        final Optional<Faculty> facultyOptional = repository.findById(id);
        if (facultyOptional.isPresent()) {
            repository.deleteById(id);
            return facultyOptional.get();
        }
        return null;
    }
     public List<Faculty> findByColor(String color){
         return repository.findByColor(color);
     }
}

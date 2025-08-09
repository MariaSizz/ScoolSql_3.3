package ru.hogwarts.school_sql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school_sql.model.Faculty;
import ru.hogwarts.school_sql.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {
     private final FacultyRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
     public FacultyService(FacultyRepository repository){
         this.repository = repository;
     }

    public Faculty createFaculty(Faculty faculty){
        logger.info("Was invoked method for create faculty");
         return repository.save(faculty);
    }
    public Faculty findFaculty(Long id){
        logger.info("Was invoked method for find faculty");
         return repository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty){
        logger.info("Was invoked method for edit faculty");
        if (repository.existsById(faculty.getId())){
            return repository.save(faculty);
        }
        return null;
    }
    public Faculty deleteFaculty(Long id){
        logger.info("Was invoked method for delete faculty");
        final Optional<Faculty> facultyOptional = repository.findById(id);
        if (facultyOptional.isPresent()) {
            repository.deleteById(id);
            return facultyOptional.get();
        }
        return null;
    }
     public List<Faculty> findByColor(String color){
         logger.info("Was invoked method for find faculty by color");
         return repository.findByColor(color);
     }
     public List<Faculty> findByNameOrColor(String query){
         logger.info("Was invoked method for find faculty by name or color");
         return repository.findByNameIgnoreCaseOrColorIgnoreCase(query,query);
     }
}

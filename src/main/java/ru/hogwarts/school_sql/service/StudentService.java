package ru.hogwarts.school_sql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school_sql.model.Faculty;
import ru.hogwarts.school_sql.model.Student;
import ru.hogwarts.school_sql.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class StudentService {
    private final StudentRepository repository;
    private Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        return repository.save(student);
    }

    public Student findStudent(Long id) {
        logger.info("Was invoked method for find student");
        return repository.findById(id).orElse(null);
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student");
        if (repository.existsById(student.getId())) {
            return repository.save(student);
        }
        return null;
    }

    public Student deleteStudent(Long id) {
        logger.info("Was invoked method for delete student");
        final Optional<Student> studentOptional = repository.findById(id);
        if (studentOptional.isPresent()) {
            repository.deleteById(id);
            return studentOptional.get();
        }
        return null;
    }

    public List<Student> findByAge(int age) {
        logger.info("Was invoked method for find student by age");
        return repository.findByAge(age);
    }

    public List<Student> findByAgeBetween(int minAge, int maxAge){
        logger.info("Was invoked method for find student by age between");
        return repository.findByAgeBetween(minAge,maxAge);
    }
    public Faculty getStudentFaculty(Long studentId){
        logger.info("Was invoked method for get student by faculty");
        return repository.findById(studentId).get().getFaculty();
    }

    public Integer getTotalStudentsCount() {
        logger.info("Was invoked method for get total students");
        return repository.getTotalStudentsCount().orElse(0);
    }

    public Double getAverageStudentAge() {
        logger.info("Was invoked method for get average student age");
        return repository.getAverageStudentAge().orElse(0.0);
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        return repository.getLastFiveStudents();
    }
     public List<String> findStudentNamesStartingWithA() {
         logger.info("Was invoked method for find student names starting with A");
return repository.findAll().stream().map(Student::getName).map(String::toUpperCase).filter(name -> name.startsWith("A")).sorted().toList();
     }
     public Double getAverageStudentAgeUsingStream() {
         logger.info("Was invoked method for get average student age using stream");
         return repository.findAll().stream().mapToInt(Student::getAge).average().orElse(0.0);
     }
     public Integer calculationSum(){
         logger.info("Was invoked method for calculation sum");
         return Stream.iterate(1, a -> a +1).limit(1_000_000).parallel().reduce(0, (a, b) -> a + b );
     }

     public void printStudentsParallel(){
         final List<Student> students = repository.findAll();
         for (int i = 0; i < 2; i++) {
             System.out.println("Основной поток " + students.get(i).getName());
         }
         new Thread(() ->{
                          for (int i = 2; i < 4; i++) {
                 System.out.println("1 параллельный поток " + students.get(i).getName());
             }
         }).start();
         new Thread(() ->{
             for (int i = 4; i < 6; i++) {
                 System.out.println("2 параллельный поток " + students.get(i).getName());
             }
         }).start();
     }
    public void printStudentsSynchronized(){
        final List<Student> students = repository.findAll();
        for (int i = 0; i < 2; i++) {
            printStudentNameSynchronized("Основной поток", students.get(i).getName());
        }
        new Thread(() ->{
            for (int i = 2; i < 4; i++) {
                printStudentNameSynchronized("1 параллельный поток", students.get(i).getName());
            }
        }).start();
        new Thread(() ->{
            for (int i = 4; i < 6; i++) {
                printStudentNameSynchronized("2 параллельный поток", students.get(i).getName());
            }
        }).start();
    }
    private synchronized void printStudentNameSynchronized(String threadName, String studentName){
        System.out.println(threadName + " " + studentName);
    }
}

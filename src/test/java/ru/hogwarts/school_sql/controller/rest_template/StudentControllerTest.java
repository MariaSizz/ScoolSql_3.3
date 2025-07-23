package ru.hogwarts.school_sql.controller.rest_template;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.hogwarts.school_sql.model.Faculty;
import ru.hogwarts.school_sql.model.Student;
import ru.hogwarts.school_sql.repository.FacultyRepository;
import ru.hogwarts.school_sql.repository.StudentRepository;
import ru.hogwarts.school_sql.service.StudentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
        }
)
class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private StudentService studentService;
    @MockitoBean
    private StudentRepository studentRepository;
    @MockitoBean
    private FacultyRepository facultyRepository;

    @Test
    void shouldCreateStudentSuccessfully() {
        Student inputStudent = createTestStudent();
        Student expectedStudent = createTestStudent();

        when(studentService.createStudent(any(Student.class))).thenReturn(expectedStudent);
        when(studentRepository.save(any(Student.class))).thenReturn(expectedStudent);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student",
                inputStudent,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Harry Potter");
        assertThat(response.getBody().getAge()).isEqualTo(17);
    }

    @Test
    void shouldReturnStudentByIdSuccessfully() {
        Student expectedStudent = createTestStudent();

        when(studentService.findStudent(1L)).thenReturn(expectedStudent);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/1",
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Harry Potter");
        assertThat(response.getBody().getAge()).isEqualTo(17);
    }

    @Test
    void shouldReturnNotFoundWhenStudentDoesNotExist() {
        when(studentService.findStudent(1L)).thenReturn(null);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/1",
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateStudentSuccessfully() {
        Student updatedStudent = createTestStudent();
        updatedStudent.setAge(18);

        when(studentService.editStudent(any(Student.class))).thenReturn(updatedStudent);

        HttpEntity<Student> requestEntity = new HttpEntity<>(updatedStudent);
        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.PUT,
                requestEntity,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAge()).isEqualTo(18);
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingNonExistentStudent() {
        Student student = createTestStudent();

        when(studentService.editStudent(any(Student.class))).thenReturn(null);

        HttpEntity<Student> requestEntity = new HttpEntity<>(student);
        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.PUT,
                requestEntity,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldDeleteStudentSuccessfully() {
        Student deletedStudent = createTestStudent();

        when(studentService.deleteStudent(1L)).thenReturn(deletedStudent);

        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/1",
                HttpMethod.DELETE,
                null,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentStudent() {
        when(studentService.deleteStudent(1L)).thenReturn(null);

        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/1",
                HttpMethod.DELETE,
                null,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFilterStudentsByAgeSuccessfully() {
        List<Student> expectedStudents = List.of(createTestStudent());

        when(studentService.findByAge(17)).thenReturn(expectedStudents);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student" + "/filter?age=" + 17,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getAge()).isEqualTo(17);
    }

    @Test
    void shouldFilterStudentsByAgeBetweenSuccessfully() {
        List<Student> expectedStudents = List.of(createTestStudent());

        when(studentService.findByAgeBetween(15, 20)).thenReturn(expectedStudents);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student" +  "/age-between?min=" + 15 + "&max=" + 20,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnStudentFacultySuccessfully() {
        Faculty expectedFaculty = createTestFaculty();

        when(studentService.getStudentFaculty(1L)).thenReturn(expectedFaculty);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/1/faculty",
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Gryffindor");
    }

    private Student createTestStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setName("Harry Potter");
        student.setAge(17);
        return student;
    }

    private Faculty createTestFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("red");
        return faculty;
    }

}
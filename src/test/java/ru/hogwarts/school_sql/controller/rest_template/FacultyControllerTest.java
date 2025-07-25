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
import ru.hogwarts.school_sql.service.FacultyService;

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
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private FacultyService facultyService;
    @MockitoBean
    private StudentRepository studentRepository;
    @MockitoBean
    private FacultyRepository facultyRepository;

    @Test
    void shouldCreateFacultySuccessfully() {
        Faculty inputFaculty = createTestFaculty();
        Faculty expectedFaculty = createTestFaculty();
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(expectedFaculty);

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculty",
                inputFaculty,
                Faculty.class
        );

       assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       assertThat(response.getBody()).isNotNull();
       assertThat(response.getBody().getId()).isEqualTo(1L);
       assertThat(response.getBody().getName()).isEqualTo("Gryffindor");
       assertThat(response.getBody().getColor()).isEqualTo("red");
    }

    @Test
    void shouldReturnFacultyByIdSuccessfully() {
        Faculty expectedFaculty = createTestFaculty();
        when(facultyService.findFaculty(1L)).thenReturn(expectedFaculty);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/1",
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Gryffindor");
        assertThat(response.getBody().getColor()).isEqualTo("red");
    }

    @Test
    void shouldReturnNotFoundWhenFacultyDoesNotExist() {
        when(facultyService.findFaculty(1L)).thenReturn(null);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/1",
                Faculty.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateFacultySuccessfully() {
        Faculty updatedFaculty = createTestFaculty();
        updatedFaculty.setColor("blue");
        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(updatedFaculty);

        HttpEntity<Faculty> requestEntity = new HttpEntity<>(updatedFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                requestEntity,
                Faculty.class
        );

       assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
       assertThat(response.getBody()).isNotNull();
       assertThat(response.getBody().getColor()).isEqualTo("blue");
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingNonExistentFaculty() {
        Faculty faculty = createTestFaculty();
        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(null);

        HttpEntity<Faculty> requestEntity = new HttpEntity<>(faculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                requestEntity,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldDeleteFacultySuccessfully() {
        Faculty deletedFaculty = createTestFaculty();
        when(facultyService.deleteFaculty(1L)).thenReturn(deletedFaculty);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/1",
                HttpMethod.DELETE,
                null,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentFaculty() {
        when(facultyService.deleteFaculty(1L)).thenReturn(null);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/1",
                HttpMethod.DELETE,
                null,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFilterFacultiesByColorSuccessfully() {
        List<Faculty> expectedFaculties = List.of(createTestFaculty());
        when(facultyService.findByColor("red")).thenReturn(expectedFaculties);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/filter?color=red",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getColor()).isEqualTo("red");
    }

    @Test
    void shouldSearchFacultiesByNameOrColorSuccessfully() {
        List<Faculty> expectedFaculties = List.of(createTestFaculty());
        when(facultyService.findByNameOrColor("red")).thenReturn(expectedFaculties);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/search?query=red",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
    }

    private Faculty createTestFaculty() {
        return new Faculty(1L,"Gryffindor","red");
    }

    private Student createTestStudent() {
        return new Student(1L,"Harry Potter",17);
    }
}
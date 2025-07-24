package ru.hogwarts.school_sql.controller.web_mvc;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school_sql.controller.StudentController;
import ru.hogwarts.school_sql.model.Faculty;
import ru.hogwarts.school_sql.model.Student;
import ru.hogwarts.school_sql.service.StudentService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @InjectMocks
    private StudentController studentController;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StudentService studentService;

    @Test
    void shouldCreateStudentSuccessfully() throws Exception {
        Faculty faculty = new Faculty(2L,"sliz","red");
        Student student = new Student(1L,"Ivan",18,faculty);
        faculty.setStudents(Set.of(student));
        JSONObject userObject = new JSONObject();
        userObject.put("name", "Ivan");
        userObject.put("age", 18);

        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .content(userObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ivan"));
    }
    @Test
    void shouldReturnStudentByIdSuccessfully() throws Exception {
        Faculty faculty = new Faculty(2L,"sliz","red");
        Student student = new Student(1L,"Ivan",18,faculty);
        faculty.setStudents(Set.of(student));
        JSONObject userObject = new JSONObject();
        userObject.put("name", "Ivan");
        userObject.put("age", 18);

        when(studentService.findStudent(any(Long.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}",1L)
                        .content(userObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.age").value(18))
                .andExpect(jsonPath("$.name").value("Ivan"));
    }

    @Test
    void shouldUpdateStudentSuccessfully() throws Exception {
        Faculty faculty = new Faculty(2L,"sliz","red");
        Student student = new Student(1L,"Sveta",19,faculty);
        faculty.setStudents(Set.of(student));
        JSONObject userObject = new JSONObject();
        userObject.put("name", "Sveta");
        userObject.put("age", 19);
        userObject.put("id", 1L);

        when(studentService.editStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.put("/student")
                        .content(userObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.age").value(19))
                .andExpect(jsonPath("$.name").value("Sveta"));
    }
    @Test
    void shouldDeleteStudentSuccessfully() throws Exception {
        Faculty faculty = new Faculty(2L,"sliz","red");
        Student student = new Student(1L,"Sveta",19,faculty);
        faculty.setStudents(Set.of(student));
        JSONObject userObject = new JSONObject();
        userObject.put("name", "Sveta");
        userObject.put("age", 19);
        userObject.put("id", 1L);

        when(studentService.deleteStudent(any(Long.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}",1L)
                        .content(userObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.age").value(19))
                .andExpect(jsonPath("$.name").value("Sveta"));
    }
    @Test
    void shouldFilterStudentByAgeSuccessfully() throws Exception {
        Faculty faculty = new Faculty(2L,"sliz","red");
        Student student = new Student(1L,"Sveta",19,faculty);
        faculty.setStudents(Set.of(student));
        JSONObject userObject = new JSONObject();
        userObject.put("name", "Sveta");
        userObject.put("age", 19);
        userObject.put("id", 1L);

        when(studentService.findByAge(any(Integer.class))).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/filter").param("age", "19")
                        .content(userObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("[0].age").value(19))
                .andExpect(jsonPath("[0].name").value("Sveta"));
    }

    @Test
    void shouldFilterStudentsByAgeBetweenSuccessfully() throws Exception {
        List<Student> expectedStudents = List.of(createTestStudent());

        when(studentService.findByAgeBetween(10, 20)).thenReturn(expectedStudents);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/age-between")
                        .param("min", String.valueOf(10))
                        .param("max", String.valueOf(20)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].age").value(19));
    }

    @Test
    void shouldFilterStudentsByAgeBetweenNotFound() throws Exception {
        List<Student> expectedStudents = List.of(createTestStudent());

        when(studentService.findByAgeBetween(10, 20)).thenReturn(expectedStudents);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/age-between")
                        .param("min", String.valueOf(10))
                        .param("max", String.valueOf(18)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnStudentFacultySuccessfully() throws Exception {
        Faculty expectedFaculty = createTestFaculty();

        when(studentService.getStudentFaculty(1L)).thenReturn(expectedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}/faculty", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("sliz"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    private Faculty createTestFaculty() {
        return new Faculty(1L,"sliz","red");
    }

    private Student createTestStudent() {
        return new Student(1L,"Sveta",19);
    }
}

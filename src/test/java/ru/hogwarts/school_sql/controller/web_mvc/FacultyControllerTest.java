package ru.hogwarts.school_sql.controller.web_mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school_sql.controller.FacultyController;
import ru.hogwarts.school_sql.model.Faculty;
import ru.hogwarts.school_sql.service.FacultyService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateFacultySuccessfully() throws Exception {
        Faculty inputFaculty = createTestFaculty();
        Faculty expectedFaculty = createTestFaculty();

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(expectedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(objectMapper.writeValueAsString(inputFaculty))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    void shouldReturnFacultyByIdSuccessfully() throws Exception {
        Faculty expectedFaculty = createTestFaculty();

        when(facultyService.findFaculty(1L)).thenReturn(expectedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    void shouldReturnNotFoundWhenFacultyDoesNotExist() throws Exception {
        when(facultyService.findFaculty(1L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateFacultySuccessfully() throws Exception {
        Faculty updatedFaculty = createTestFaculty();
        updatedFaculty.setColor("blue");

        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(objectMapper.writeValueAsString(updatedFaculty))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.color").value("blue"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingNonExistentFaculty() throws Exception {
        Faculty faculty = createTestFaculty();

        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteFacultySuccessfully() throws Exception {
        Faculty deletedFaculty = createTestFaculty();

        when(facultyService.deleteFaculty(1L)).thenReturn(deletedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentFaculty() throws Exception {
        when(facultyService.deleteFaculty(1L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterFacultiesByColorSuccessfully() throws Exception {
        List<Faculty> expectedFaculties = List.of(createTestFaculty());

        when(facultyService.findByColor("red")).thenReturn(expectedFaculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter")
                        .param("color", "red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].color").value("red"));
    }

    @Test
    void shouldSearchFacultiesByNameOrColorSuccessfully() throws Exception {
        List<Faculty> expectedFaculties = List.of(createTestFaculty());

        when(facultyService.findByNameOrColor("red")).thenReturn(expectedFaculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/search")
                        .param("query", "red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].color").value("red"));
    }

    private Faculty createTestFaculty() {
        return new Faculty(1L,"Gryffindor","red");
    }
}
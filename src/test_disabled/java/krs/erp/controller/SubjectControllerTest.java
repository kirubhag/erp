package krs.erp.controller;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.model.Subject;
import krs.erp.repository.SubjectRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Subject testSubject;

    @BeforeEach
    void setUp() {
        subjectRepository.deleteAll();
        
        testSubject = new Subject();
        testSubject.setSubjectCode("TEST-MATH");
        testSubject.setSubjectName("Test Mathematics");
        testSubject.setDescription("Test math subject");
        testSubject.setGradeLevel("Grade 1");
        testSubject.setCategory("Core");
        testSubject.setCredits(3);
        testSubject.setHoursPerWeek(5);
        testSubject.setDifficultyLevel("Easy");
        testSubject.setIsMandatory(true);
        testSubject.markAsActive();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllSubjects() throws Exception {
        subjectRepository.save(testSubject);

        mockMvc.perform(get("/api/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].subjectCode", is("TEST-MATH")))
                .andExpect(jsonPath("$.content[0].subjectName", is("Test Mathematics")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSubjectById() throws Exception {
        Subject saved = subjectRepository.save(testSubject);

        mockMvc.perform(get("/api/subjects/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectCode", is("TEST-MATH")))
                .andExpect(jsonPath("$.subjectName", is("Test Mathematics")))
                .andExpect(jsonPath("$.gradeLevel", is("Grade 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSubjectById_NotFound() throws Exception {
        mockMvc.perform(get("/api/subjects/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateSubject() throws Exception {
        String subjectJson = objectMapper.writeValueAsString(testSubject);

        mockMvc.perform(post("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subjectCode", is("TEST-MATH")))
                .andExpect(jsonPath("$.subjectName", is("Test Mathematics")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateSubject_DuplicateCode() throws Exception {
        subjectRepository.save(testSubject);

        Subject duplicate = new Subject();
        duplicate.setSubjectCode("TEST-MATH");
        duplicate.setSubjectName("Duplicate Math");
        duplicate.setGradeLevel("Grade 2");
        duplicate.markAsActive();

        String subjectJson = objectMapper.writeValueAsString(duplicate);

        mockMvc.perform(post("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateSubject() throws Exception {
        Subject saved = subjectRepository.save(testSubject);

        saved.setSubjectName("Updated Mathematics");
        saved.setCredits(4);

        String subjectJson = objectMapper.writeValueAsString(saved);

        mockMvc.perform(put("/api/subjects/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectName", is("Updated Mathematics")))
                .andExpect(jsonPath("$.credits", is(4)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateSubject_NotFound() throws Exception {
        String subjectJson = objectMapper.writeValueAsString(testSubject);

        mockMvc.perform(put("/api/subjects/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteSubject() throws Exception {
        Subject saved = subjectRepository.save(testSubject);

        mockMvc.perform(delete("/api/subjects/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete
        Subject deleted = subjectRepository.findById(saved.getId()).orElse(null);
        assert deleted != null;
        assert deleted.isDeleted();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetActiveSubjects() throws Exception {
        subjectRepository.save(testSubject);

        Subject inactive = new Subject();
        inactive.setSubjectCode("INACTIVE-MATH");
        inactive.setSubjectName("Inactive Math");
        inactive.setGradeLevel("Grade 2");
        inactive.markAsInactive();
        subjectRepository.save(inactive);

        mockMvc.perform(get("/api/subjects/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].subjectCode", is("TEST-MATH")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSubjectsByGrade() throws Exception {
        subjectRepository.save(testSubject);

        Subject grade2Subject = new Subject();
        grade2Subject.setSubjectCode("G2-MATH");
        grade2Subject.setSubjectName("Grade 2 Math");
        grade2Subject.setGradeLevel("Grade 2");
        grade2Subject.markAsActive();
        subjectRepository.save(grade2Subject);

        mockMvc.perform(get("/api/subjects/grade/Grade 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gradeLevel", is("Grade 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchSubjects() throws Exception {
        subjectRepository.save(testSubject);

        mockMvc.perform(get("/api/subjects/search?query=math"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].subjectCode", containsStringIgnoringCase("MATH")));
    }
}

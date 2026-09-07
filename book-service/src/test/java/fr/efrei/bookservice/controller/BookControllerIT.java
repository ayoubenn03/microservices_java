package fr.efrei.bookservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.efrei.bookservice.dto.BookRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullCrudAndStockLifecycle() throws Exception {
        BookRequestDTO createRequest = new BookRequestDTO("1984", "George Orwell", "978-0451524935", 2);

        String createResponse = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalCopies").value(2))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/books/{id}/decrement-stock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(1));

        mockMvc.perform(patch("/api/books/{id}/decrement-stock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(0));

        mockMvc.perform(patch("/api/books/{id}/decrement-stock", id))
                .andExpect(status().isConflict());

        mockMvc.perform(patch("/api/books/{id}/increment-stock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(1));

        BookRequestDTO updateRequest = new BookRequestDTO("1984", "George Orwell", "978-0451524935", 5);
        mockMvc.perform(put("/api/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCopies").value(5));

        mockMvc.perform(delete("/api/books/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBook_withoutTotalCopies_returnsBadRequest() throws Exception {
        BookRequestDTO invalidRequest = new BookRequestDTO("", "", "", null);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}

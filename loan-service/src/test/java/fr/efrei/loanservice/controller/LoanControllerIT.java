package fr.efrei.loanservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import fr.efrei.loanservice.client.BookClient;
import fr.efrei.loanservice.dto.BookDTO;
import fr.efrei.loanservice.dto.LoanRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookClient bookClient;

    @Test
    void createLoan_thenReturn_succeeds() throws Exception {
        BookDTO availableBook = new BookDTO(1L, "1984", "George Orwell", "978-0451524935", 2, 1);
        when(bookClient.getBook(1L)).thenReturn(availableBook);
        when(bookClient.decrementStock(1L)).thenReturn(availableBook);
        when(bookClient.incrementStock(1L)).thenReturn(availableBook);

        LoanRequestDTO requestDTO = new LoanRequestDTO(1L, "Alice");

        String createResponse = mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.bookTitle").value("1984"))
                .andReturn().getResponse().getContentAsString();

        Long loanId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(patch("/api/loans/{id}/return", loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));

        // Tentative de rendre deux fois le même emprunt
        mockMvc.perform(patch("/api/loans/{id}/return", loanId))
                .andExpect(status().isConflict());
    }

    @Test
    void createLoan_whenStockExhausted_returnsConflict() throws Exception {
        BookDTO exhaustedBook = new BookDTO(2L, "Brave New World", "Aldous Huxley", "978-0060850524", 1, 0);
        when(bookClient.getBook(2L)).thenReturn(exhaustedBook);

        LoanRequestDTO requestDTO = new LoanRequestDTO(2L, "Bob");

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void createLoan_whenBookDoesNotExist_returnsBadRequest() throws Exception {
        Request feignRequest = Request.create(
                Request.HttpMethod.GET, "/api/books/999", Collections.emptyMap(), null, null, null);
        when(bookClient.getBook(999L))
                .thenThrow(new FeignException.NotFound("Not Found", feignRequest, null, Collections.emptyMap()));

        LoanRequestDTO requestDTO = new LoanRequestDTO(999L, "Carol");

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}

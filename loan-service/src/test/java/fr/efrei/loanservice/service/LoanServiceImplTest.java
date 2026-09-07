package fr.efrei.loanservice.service;

import feign.FeignException;
import feign.Request;
import fr.efrei.loanservice.client.BookClient;
import fr.efrei.loanservice.dto.BookDTO;
import fr.efrei.loanservice.dto.LoanRequestDTO;
import fr.efrei.loanservice.exception.BookNotAvailableException;
import fr.efrei.loanservice.repository.LoanRepository;
import fr.efrei.loanservice.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookClient bookClient;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void createLoan_whenBookHasNoAvailableCopies_throwsBookNotAvailableExceptionAndNeverDecrements() {
        LoanRequestDTO requestDTO = new LoanRequestDTO(1L, "Alice");
        BookDTO book = new BookDTO(1L, "1984", "George Orwell", "978-0451524935", 2, 0);
        when(bookClient.getBook(1L)).thenReturn(book);

        assertThatThrownBy(() -> loanService.createLoan(requestDTO))
                .isInstanceOf(BookNotAvailableException.class);

        verify(bookClient, never()).decrementStock(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void createLoan_whenDecrementStockReturnsConflict_throwsBookNotAvailableExceptionAndDoesNotCreateLoan() {
        // Cas de concurrence (TOCTOU) : le stock semblait disponible à l'étape 1,
        // mais book-service répond 409 lors du decrement-stock effectif.
        LoanRequestDTO requestDTO = new LoanRequestDTO(1L, "Alice");
        BookDTO book = new BookDTO(1L, "1984", "George Orwell", "978-0451524935", 2, 1);
        when(bookClient.getBook(1L)).thenReturn(book);
        when(bookClient.decrementStock(1L)).thenThrow(buildConflictException());

        assertThatThrownBy(() -> loanService.createLoan(requestDTO))
                .isInstanceOf(BookNotAvailableException.class);

        verify(loanRepository, never()).save(any());
    }

    private FeignException.Conflict buildConflictException() {
        Request request = Request.create(
                Request.HttpMethod.PATCH,
                "/api/books/1/decrement-stock",
                Collections.emptyMap(),
                null,
                null,
                null
        );
        return new FeignException.Conflict("Conflict", request, null, Collections.emptyMap());
    }
}

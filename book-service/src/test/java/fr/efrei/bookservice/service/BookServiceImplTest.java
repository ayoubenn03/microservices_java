package fr.efrei.bookservice.service;

import fr.efrei.bookservice.exception.ResourceNotFoundException;
import fr.efrei.bookservice.exception.StockUnavailableException;
import fr.efrei.bookservice.model.Book;
import fr.efrei.bookservice.repository.BookRepository;
import fr.efrei.bookservice.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void decrementStock_whenNoAvailableCopies_throwsStockUnavailableException() {
        Book book = new Book(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 3, 0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.decrementStock(1L))
                .isInstanceOf(StockUnavailableException.class);

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void decrementStock_whenCopiesAvailable_decrementsAndSaves() {
        Book book = new Book(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 3, 2);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = bookService.decrementStock(1L);

        assertThat(response.getAvailableCopies()).isEqualTo(1);
    }

    @Test
    void decrementStock_whenBookNotFound_throwsResourceNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.decrementStock(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void incrementStock_neverExceedsTotalCopies() {
        Book book = new Book(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 3, 3);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = bookService.incrementStock(1L);

        assertThat(response.getAvailableCopies()).isEqualTo(3);
    }
}

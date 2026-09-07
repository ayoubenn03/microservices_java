package fr.efrei.bookservice.service;

import fr.efrei.bookservice.dto.BookRequestDTO;
import fr.efrei.bookservice.dto.BookResponseDTO;

import java.util.List;

public interface BookService {
    List<BookResponseDTO> getAllBooks();
    BookResponseDTO getBookById(Long id);
    BookResponseDTO getBookByIsbn(String isbn);
    BookResponseDTO createBook(BookRequestDTO requestDTO);
    BookResponseDTO updateBook(Long id, BookRequestDTO requestDTO);
    void deleteBook(Long id);
    BookResponseDTO decrementStock(Long id);
    BookResponseDTO incrementStock(Long id);
}

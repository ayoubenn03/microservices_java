package fr.efrei.bookservice.controller;

import fr.efrei.bookservice.dto.BookRequestDTO;
import fr.efrei.bookservice.dto.BookResponseDTO;
import fr.efrei.bookservice.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponseDTO> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO requestDTO) {
        BookResponseDTO createdBook = bookService.createBook(requestDTO);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDTO requestDTO) {
        return ResponseEntity.ok(bookService.updateBook(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/decrement-stock")
    public ResponseEntity<BookResponseDTO> decrementStock(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.decrementStock(id));
    }

    @PatchMapping("/{id}/increment-stock")
    public ResponseEntity<BookResponseDTO> incrementStock(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.incrementStock(id));
    }
}

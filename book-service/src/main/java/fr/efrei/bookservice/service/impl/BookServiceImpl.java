package fr.efrei.bookservice.service.impl;

import fr.efrei.bookservice.dto.BookRequestDTO;
import fr.efrei.bookservice.dto.BookResponseDTO;
import fr.efrei.bookservice.exception.ResourceNotFoundException;
import fr.efrei.bookservice.exception.StockUnavailableException;
import fr.efrei.bookservice.model.Book;
import fr.efrei.bookservice.repository.BookRepository;
import fr.efrei.bookservice.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapToDTO(book);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
        return mapToDTO(book);
    }

    @Override
    public BookResponseDTO createBook(BookRequestDTO requestDTO) {
        Book book = new Book(
                null,
                requestDTO.getTitle(),
                requestDTO.getAuthor(),
                requestDTO.getIsbn(),
                requestDTO.getTotalCopies(),
                requestDTO.getTotalCopies()
        );
        Book savedBook = bookRepository.save(book);
        return mapToDTO(savedBook);
    }

    @Override
    public BookResponseDTO updateBook(Long id, BookRequestDTO requestDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        book.setTitle(requestDTO.getTitle());
        book.setAuthor(requestDTO.getAuthor());
        book.setIsbn(requestDTO.getIsbn());
        book.setTotalCopies(requestDTO.getTotalCopies());
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        Book updatedBook = bookRepository.save(book);
        return mapToDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

    @Override
    public BookResponseDTO decrementStock(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        try {
            book.decrementAvailableCopies();
        } catch (IllegalStateException ex) {
            throw new StockUnavailableException("No available copies left for book with id: " + id);
        }
        Book updatedBook = bookRepository.save(book);
        return mapToDTO(updatedBook);
    }

    @Override
    public BookResponseDTO incrementStock(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.incrementAvailableCopies();
        Book updatedBook = bookRepository.save(book);
        return mapToDTO(updatedBook);
    }

    private BookResponseDTO mapToDTO(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}

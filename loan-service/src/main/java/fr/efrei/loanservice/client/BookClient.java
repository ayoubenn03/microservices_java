package fr.efrei.loanservice.client;

import fr.efrei.loanservice.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

@FeignClient(name = "book-service")
public interface BookClient {

    @GetMapping("/api/books/{id}")
    BookDTO getBook(@PathVariable("id") Long id);

    @PatchMapping("/api/books/{id}/decrement-stock")
    BookDTO decrementStock(@PathVariable("id") Long id);

    @PatchMapping("/api/books/{id}/increment-stock")
    BookDTO incrementStock(@PathVariable("id") Long id);
}

package fr.efrei.loanservice.controller;

import fr.efrei.loanservice.dto.LoanRequestDTO;
import fr.efrei.loanservice.dto.LoanResponseDTO;
import fr.efrei.loanservice.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<LoanResponseDTO>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @GetMapping("/member/{memberName}")
    public ResponseEntity<List<LoanResponseDTO>> getLoansByMember(@PathVariable String memberName) {
        return ResponseEntity.ok(loanService.getLoansByMember(memberName));
    }

    @PostMapping
    public ResponseEntity<LoanResponseDTO> createLoan(@Valid @RequestBody LoanRequestDTO requestDTO) {
        LoanResponseDTO createdLoan = loanService.createLoan(requestDTO);
        return new ResponseEntity<>(createdLoan, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponseDTO> returnLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnLoan(id));
    }
}

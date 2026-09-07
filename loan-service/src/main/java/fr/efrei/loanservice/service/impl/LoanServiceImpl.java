package fr.efrei.loanservice.service.impl;

import fr.efrei.loanservice.client.BookClient;
import fr.efrei.loanservice.dto.BookDTO;
import fr.efrei.loanservice.dto.LoanRequestDTO;
import fr.efrei.loanservice.dto.LoanResponseDTO;
import fr.efrei.loanservice.exception.BookNotAvailableException;
import fr.efrei.loanservice.exception.InvalidBookException;
import fr.efrei.loanservice.exception.LoanAlreadyReturnedException;
import fr.efrei.loanservice.exception.ResourceNotFoundException;
import fr.efrei.loanservice.model.Loan;
import fr.efrei.loanservice.model.LoanStatus;
import fr.efrei.loanservice.repository.LoanRepository;
import fr.efrei.loanservice.service.LoanService;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookClient bookClient;

    public LoanServiceImpl(LoanRepository loanRepository, BookClient bookClient) {
        this.loanRepository = loanRepository;
        this.bookClient = bookClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponseDTO getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
        return mapToDTO(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getLoansByMember(String memberName) {
        return loanRepository.findByMemberName(memberName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LoanResponseDTO createLoan(LoanRequestDTO requestDTO) {
        BookDTO book;
        try {
            book = bookClient.getBook(requestDTO.getBookId());
        } catch (FeignException.NotFound ex) {
            throw new InvalidBookException("Book not found with id: " + requestDTO.getBookId());
        }

        if (book.getAvailableCopies() == null || book.getAvailableCopies() == 0) {
            throw new BookNotAvailableException(
                    "Aucun exemplaire disponible pour ce livre (id " + requestDTO.getBookId() + ")");
        }

        try {
            bookClient.decrementStock(requestDTO.getBookId());
        } catch (FeignException.Conflict ex) {
            throw new BookNotAvailableException(
                    "Aucun exemplaire disponible pour ce livre (id " + requestDTO.getBookId() + ")");
        }

        LocalDate loanDate = LocalDate.now();
        Loan loan = new Loan(
                null,
                requestDTO.getBookId(),
                book.getTitle(),
                requestDTO.getMemberName(),
                loanDate,
                loanDate.plusDays(14),
                null,
                LoanStatus.ACTIVE
        );

        Loan savedLoan = loanRepository.save(loan);
        return mapToDTO(savedLoan);
    }

    @Override
    public LoanResponseDTO returnLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new LoanAlreadyReturnedException("Loan with id " + id + " has already been returned");
        }

        bookClient.incrementStock(loan.getBookId());

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());
        Loan updatedLoan = loanRepository.save(loan);

        return new LoanResponseDTO(
                updatedLoan.getId(),
                updatedLoan.getBookId(),
                updatedLoan.getBookTitle(),
                updatedLoan.getMemberName(),
                updatedLoan.getLoanDate(),
                updatedLoan.getDueDate(),
                updatedLoan.getReturnDate(),
                updatedLoan.getStatus()
        );
    }

    private LoanResponseDTO mapToDTO(Loan loan) {
        return new LoanResponseDTO(
                loan.getId(),
                loan.getBookId(),
                loan.getBookTitle(),
                loan.getMemberName(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }
}

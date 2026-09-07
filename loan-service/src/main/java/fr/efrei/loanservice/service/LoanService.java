package fr.efrei.loanservice.service;

import fr.efrei.loanservice.dto.LoanRequestDTO;
import fr.efrei.loanservice.dto.LoanResponseDTO;

import java.util.List;

public interface LoanService {
    List<LoanResponseDTO> getAllLoans();
    LoanResponseDTO getLoanById(Long id);
    List<LoanResponseDTO> getLoansByMember(String memberName);
    LoanResponseDTO createLoan(LoanRequestDTO requestDTO);
    LoanResponseDTO returnLoan(Long id);
}

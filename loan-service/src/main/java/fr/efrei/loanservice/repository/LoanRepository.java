package fr.efrei.loanservice.repository;

import fr.efrei.loanservice.model.Loan;
import fr.efrei.loanservice.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberName(String memberName);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByStatus(LoanStatus status);
}

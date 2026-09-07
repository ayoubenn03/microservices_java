package fr.efrei.loanservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoanRequestDTO {

    @NotNull(message = "Book ID is mandatory")
    private Long bookId;

    @NotBlank(message = "Member name is mandatory")
    private String memberName;

    public LoanRequestDTO() {
    }

    public LoanRequestDTO(Long bookId, String memberName) {
        this.bookId = bookId;
        this.memberName = memberName;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}

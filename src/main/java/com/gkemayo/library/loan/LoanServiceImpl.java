package com.gkemayo.library.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service("loanService")
@Transactional
public class LoanServiceImpl implements ILoanService{
    @Autowired
    private ILoanDao loanDao;

    @Override
    public List<Loan> findAllLoansByEndDateBefore(LocalDate maxEndDate) {
        return loanDao.findByEndDateBefore(maxEndDate);
    }

    @Override
    public List<Loan> getAllOpenLoansOfThisCustomer(String mail, LoanStatus status) {
        return loanDao.getAllOpenLoansOfThisCustomer(mail,status);
    }

    @Override
    public Loan getOpenedLoan(SimpleLoanDTO simpleLoanDTO) {
        return loanDao.getLoanByCriteria(simpleLoanDTO.getBookId(), simpleLoanDTO.getCustomerId(),
                LoanStatus.OPEN);
    }

    @Override
    public boolean checkIfLoanExists(SimpleLoanDTO simpleLoanDTO) {
        Loan loan = loanDao.getLoanByCriteria(simpleLoanDTO.getBookId(),
                simpleLoanDTO.getCustomerId(), LoanStatus.OPEN);
        return loan != null;
    }

    @Override
    public Loan saveLoan(Loan loan) {
        return loanDao.save(loan);
    }

    @Override
    public void closeLoan(Loan loan) {
        loanDao.save(loan);
    }


    public List<Loan> getAllLoans(){
        return loanDao.findAll();
    }
}

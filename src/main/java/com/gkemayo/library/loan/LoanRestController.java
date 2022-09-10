package com.gkemayo.library.loan;

import com.gkemayo.library.book.Book;
import com.gkemayo.library.customer.Customer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/loan/api")
public class LoanRestController {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoanRestController.class);

    @Autowired
    private LoanServiceImpl loanService;

    @GetMapping("/maxEndDate")
    @ApiOperation(value = "List loans realized before the indicated date", response = List.class)
    @ApiResponse(code = 200, message = "Ok: successfully listed")
    public ResponseEntity<List<LoanDTO>> searchAllBooksLoanBeforeThisDate
            (@RequestParam("date")String maxEndDateStr){
        List<Loan> loans = loanService.findAllLoansByEndDateBefore(LocalDate.parse(maxEndDateStr));
        loans.removeAll(Collections.singleton(null));
        List<LoanDTO> loanInfosDTOs = mapLoanDtosFromLoans(loans);
        return new ResponseEntity<List<LoanDTO>>(loanInfosDTOs, HttpStatus.OK);
    }

    @GetMapping("/customerLoans")
    @ApiOperation(value = "List loans realized before the indicated date", response = List.class)
    @ApiResponse(code = 200, message = "Ok: successfully listed")
    public ResponseEntity<List<LoanDTO>>  searchAllOpenedLoansOfThisCustomer
            (@RequestParam("email")String email){
        List<Loan> loans = loanService.getAllOpenLoansOfThisCustomer(email,LoanStatus.OPEN);
        loans.removeAll(Collections.singleton(null));
        List<LoanDTO> loanInfosDTOs = mapLoanDtosFromLoans(loans);
        return new ResponseEntity<List<LoanDTO>>(loanInfosDTOs,HttpStatus.OK);
    }

    @PostMapping("/addLoan")
    @ApiOperation(value = "Add a new Loan in the Library", response = LoanDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 409, message = "Conflict: the loan already exist"),
            @ApiResponse(code = 201, message = "Created: the loan is successfully inserted"),
            @ApiResponse(code = 304, message = "Not Modified: the loan is unsuccessfully inserted")})
    public ResponseEntity<Boolean> createNewLoan(@RequestBody SimpleLoanDTO simpleLoanDTORequest,
                                                 UriComponentsBuilder uriComponentsBuilder){
        boolean isLoansExists = loanService.checkIfLoanExists(simpleLoanDTORequest);
        if (isLoansExists){
            return new ResponseEntity<Boolean>(false, HttpStatus.CONFLICT);
        }
        Loan loanRequest = mapSimpleLoanDTOToLoan(simpleLoanDTORequest);
        Loan loan = loanService.saveLoan(loanRequest);
        if (loan!=null){
            return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
        }
        return new ResponseEntity<Boolean>(false,HttpStatus.NOT_MODIFIED);
    }

    @PostMapping("/closeLoan")
    public ResponseEntity<Boolean> closeLoan(@RequestBody SimpleLoanDTO simpleLoanDTORequest,
                                             UriComponentsBuilder uriComponentsBuilder){
        Loan existingLoan = loanService.getOpenedLoan(simpleLoanDTORequest);
        if (existingLoan == null){
            return new ResponseEntity<Boolean>(false, HttpStatus.NO_CONTENT);
        }
        existingLoan.setStatus(LoanStatus.CLOSE);
        Loan loan = loanService.saveLoan(existingLoan);
        if (loan != null){
            return new ResponseEntity<Boolean>(true,HttpStatus.OK);
        }
        return new ResponseEntity<Boolean>(HttpStatus.NOT_MODIFIED);
    }

    @GetMapping("/allLoans")
    public ResponseEntity<List<LoanDTO>> getAllLoans(){
        List<Loan> loans = loanService.getAllLoans();
        loans.removeAll(Collections.singleton(null));
        List<LoanDTO> loanInfosDTOs = mapLoanDtosFromLoans(loans);
        return new ResponseEntity<List<LoanDTO>>(loanInfosDTOs,HttpStatus.OK);
    }










































    private List<LoanDTO> mapLoanDtosFromLoans(List<Loan> loans) {

        Function<Loan, LoanDTO> mapperFunction = (loan) -> {
            // dans loanDTO on ajoute que les données nécessaires
            LoanDTO loanDTO = new LoanDTO();
            loanDTO.getBookDTO().setId(loan.getPk().getBook().getId());
            loanDTO.getBookDTO().setIsbn(loan.getPk().getBook().getIsbn());
            loanDTO.getBookDTO().setTitle(loan.getPk().getBook().getTitle());

            loanDTO.getCustomerDTO().setId(loan.getPk().getCustomer().getId());
            loanDTO.getCustomerDTO().setFirstName(loan.getPk().getCustomer().getFirstName());
            loanDTO.getCustomerDTO().setLastName(loan.getPk().getCustomer().getLastName());
            loanDTO.getCustomerDTO().setEmail(loan.getPk().getCustomer().getEmail());

            loanDTO.setLoanBeginDate(loan.getBeginDate());
            loanDTO.setLoanEndDate(loan.getEndDate());
            return loanDTO;
        };

        if (!CollectionUtils.isEmpty(loans)) {
            return loans.stream().map(mapperFunction).sorted().collect(Collectors.toList());
        }
        return null;
    }

    private Loan mapSimpleLoanDTOToLoan(SimpleLoanDTO simpleLoanDTO) {
        Loan loan = new Loan();
        Book book = new Book();
        book.setId(simpleLoanDTO.getBookId());
        Customer customer = new Customer();
        customer.setId(simpleLoanDTO.getCustomerId());
        LoanId loanId = new LoanId(book, customer);
        loan.setPk(loanId);
        loan.setBeginDate(simpleLoanDTO.getBeginDate());
        loan.setEndDate(simpleLoanDTO.getEndDate());
        loan.setStatus(LoanStatus.OPEN);
        return loan;
    }


}

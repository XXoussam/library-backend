package com.gkemayo.library.loan;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "LOAN")
@AssociationOverrides({
        @AssociationOverride(name = "pk.book", joinColumns = @JoinColumn(name = "BOOK_ID")),
        @AssociationOverride(name = "pk.customer", joinColumns = @JoinColumn(name = "CUSTOMER_ID"))
})
@Data
public class Loan implements Serializable {

    private static final long serialVersionUID = 144293603488149743L;

    @EmbeddedId
    private LoanId pk = new LoanId();
    @Column(name = "BEGIN_DATE", nullable = false)
    private LocalDate beginDate;
    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private LoanStatus status;




    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pk == null) ? 0 : pk.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Loan other = (Loan) obj;
        if (pk == null) {
            if (other.pk != null)
                return false;
        } else if (!pk.equals(other.pk))
            return false;
        return true;
    }

}

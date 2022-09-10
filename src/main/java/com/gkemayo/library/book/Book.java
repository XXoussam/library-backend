package com.gkemayo.library.book;

import com.gkemayo.library.category.Category;
import com.gkemayo.library.loan.Loan;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BOOK_ID")
    private Integer id;
    @Column(name = "TITLE", nullable = false)
    private String title;
    @Column(name = "ISBN", nullable = false, unique = true)
    private String isbn;
    @Column(name = "RELEASE_DATE", nullable = false)
    private LocalDate releaseDate;
    @Column(name = "REGISTER_DATE", nullable = false)
    private LocalDate registerDate;
    @Column(name = "TOTAL_EXAMPLARIES")
    private Integer totalExamplaries;
    @Column(name = "AUTHOR")
    private String author;
    @ManyToOne(optional = false)
    @JoinColumn(name = "CAT_CODE", referencedColumnName = "CODE")
    private Category category;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.book", cascade = CascadeType.ALL)
    Set<Loan> loans = new HashSet<Loan>();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
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
        Book other = (Book) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (isbn == null) {
            if (other.isbn != null)
                return false;
        } else if (!isbn.equals(other.isbn))
            return false;
        return true;
    }
}

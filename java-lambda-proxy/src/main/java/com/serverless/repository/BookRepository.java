package com.serverless.repository;

import com.serverless.model.Book;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BookRepository {

    private static final List<Book> books = Arrays.asList(
            new Book("1", "The Shining", 1, 1977, 12000),
            new Book("2", "Black House", 1, 2001, 8700),
            new Book("3", "Da Vinci Code", 2, 2003, 130000),
            new Book("4", "Angles & Demons", 2, 2000, 98000),
            new Book("5", "Deception Point", 2, 2001, 56000),
            new Book("6", "Les Fourmis", 3, 1991, 46800),
            new Book("7", "Le Jour des fourmis", 3, 1992, 48300),
            new Book("8", "La Révolution des fourmis", 3, 1996, 53000)
    );

    private static volatile BookRepository bookRepository;

    private BookRepository() {
    }

    public static BookRepository instance() {
        if (bookRepository == null) {
            synchronized (BookRepository.class) {
                if (bookRepository == null) {
                    bookRepository = new BookRepository();
                }
            }
        }
        return bookRepository;
    }

    public List<Book> getBooks() {
        return books;
    }

    public Optional<Book> getBook(String bookId) {
        return books.stream()
                .filter(book -> book.getId().equals(bookId))
                .findFirst();
    }

}


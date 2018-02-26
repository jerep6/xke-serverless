package com.serverless.handler;

import com.serverless.gateway.BaseTest;
import com.serverless.model.Book;
import com.serverless.repository.BookRepository;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class BookHandlerIT extends BaseTest {

    private BookRepository bookRepository = BookRepository.instance();

    @Test
    public void should_return_all_books() {
        //when
        Book[] books = given()
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .extract()
                .as(Book[].class);

        //then
        List<Book> booksRepo = bookRepository.getBooks();
        assertThat(books).hasSameSizeAs(booksRepo);
        assertThat(books).hasSameElementsAs(booksRepo);
    }

    @Test
    public void should_return_one_books() {
        //given
        String bookId = "2";

        //when
        Book book = given()
                .pathParam("bookId",  bookId)
                .when().get("/books/{bookId}")
                .then()
                .statusCode(200)
                .extract()
                .as(Book.class);

        //then
        Book book2 = bookRepository.getBook(bookId).get();

        assertThat(book.getId()).isEqualTo(book2.getId());
        assertThat(book.getAuthorId()).isEqualTo(book2.getAuthorId());
        assertThat(book.getTitle()).isEqualTo(book2.getTitle());
        assertThat(book.getYear()).isEqualTo(book2.getYear());
        assertThat(book.getLikes()).isEqualTo(book2.getLikes());
    }

}

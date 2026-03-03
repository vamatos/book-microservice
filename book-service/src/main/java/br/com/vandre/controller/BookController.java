package br.com.vandre.controller;


import br.com.vandre.dto.ExchangeDTO;
import br.com.vandre.environment.InstanceInformationService;
import br.com.vandre.model.Book;
import br.com.vandre.proxy.ExchangeProxy;
import br.com.vandre.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book Endpoint")
@RestController
@RequestMapping("book-service")
public class BookController {


    private final InstanceInformationService instanceInformationService;

    private final BookRepository repository;

    private final ExchangeProxy proxy;

    public BookController(InstanceInformationService instanceInformationService, BookRepository repository, ExchangeProxy proxy) {
        this.instanceInformationService = instanceInformationService;
        this.repository = repository;
        this.proxy = proxy;
    }
    @Operation(summary = "Find a book by ID and convert its price to the specified currency")
    @GetMapping(value ="/{id}/{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book findBook(@PathVariable Long id, @PathVariable String currency) {

        var port = instanceInformationService.retrieveServerPort();

        var book = repository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

        ExchangeDTO exchangeDTO = proxy.getExchange(book.getPrice(), "USD", currency);

        book.setEnvironment("BOOK: "+ port + " Exchange Port: " + exchangeDTO.getEnvironment());
        book.setCurrency(currency);
        book.setPrice(exchangeDTO.getConvertedValue());

        return book;
    }

}

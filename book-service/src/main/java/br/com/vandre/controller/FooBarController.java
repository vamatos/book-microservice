package br.com.vandre.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Tag(name = "Foo Bar Endpoint")
@RestController
@RequestMapping("book-service")
public class FooBarController {


    private Logger logger = LoggerFactory.getLogger(FooBarController.class);

    @GetMapping("/foo-bar")
//    @Retry(name= "default")
//    @Retry(name="foo-bar")
//    @Retry(name="foo-bar", fallbackMethod = "fallbackFooBar")
    @CircuitBreaker(name= "default", fallbackMethod = "fallbackFooBar")
    public String fooBar(){
        logger.info("Request to foo-bar endpoint received");
        var response = new RestTemplate().getForEntity("http://localhost:8080/foo-bar", String.class);
        return response.getBody();
    }

    public String fallbackFooBar(Exception e) {
        logger.error("Fallback method called due to exception: {}", e.getMessage());
        return "Fallback response: Service is currently unavailable. Please try again later.";
    }

}

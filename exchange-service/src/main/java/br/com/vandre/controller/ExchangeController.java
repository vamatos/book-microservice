package br.com.vandre.controller;

import br.com.vandre.environment.InstanceInformationService;
import br.com.vandre.model.Exchange;
import br.com.vandre.repository.ExchangeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Tag(name = "Exchange Endpoint")
@RestController
@RequestMapping("exchange-service")
public class ExchangeController {

    private final InstanceInformationService instanceInformationService;

    private final ExchangeRepository repository;

    private Logger logger = LoggerFactory.getLogger(ExchangeController.class);

    public ExchangeController(InstanceInformationService instanceInformationService, ExchangeRepository repository) {
        this.instanceInformationService = instanceInformationService;
        this.repository = repository;
    }

    @Operation(summary = "Convert a specified amount from one currency to another")
    @GetMapping(value = "/{amount}/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Exchange getExchange(@PathVariable BigDecimal amount, @PathVariable String from, @PathVariable String to) {

        logger.info("Received request to convert {} from {} to {}", amount, from, to);
        Exchange exchange = repository.findByFromAndTo(from, to);

        if (exchange == null) {
            throw new RuntimeException("Currency Unsupported");
        }

        BigDecimal conversionFactor = exchange.getConversionFactor();
        BigDecimal convertedValue = conversionFactor.multiply(amount);

        String port = instanceInformationService.retrieveServerPort();
        String hostName = instanceInformationService.getHostName();
        exchange.setConvertedValue(convertedValue);
        exchange.setEnvironment("PORT " + port + " Host: " + hostName);

        return exchange;
    }
}

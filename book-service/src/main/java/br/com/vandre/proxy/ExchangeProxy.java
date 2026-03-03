package br.com.vandre.proxy;

import br.com.vandre.dto.ExchangeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "exchange-service")
public interface ExchangeProxy {

    @GetMapping(value = "/exchange-service/{amount}/{from}/{to}")
    ExchangeDTO getExchange(@PathVariable Double amount, @PathVariable String from, @PathVariable String to);

}

package br.com.vandre.repository;

import br.com.vandre.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExchangeRepository extends JpaRepository<Exchange, UUID> {

    Exchange findByFromAndTo(String from, String to);
}

package br.com.vandre.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;


@Entity(name = "exchange")
public class Exchange implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name="from_currency", nullable = false, length = 3)
    private String from;

    @Column(name = "to_currency", nullable = false, length = 3)
    private String to;

    @Column(name = "conversion_factor", nullable = false)
    private BigDecimal conversionFactor;

    @Transient
    private BigDecimal convertedValue;

    @Transient
    private String environment;

    public Exchange(UUID id, String from, String to, String environment, BigDecimal conversionFactor, BigDecimal convertedValue) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.conversionFactor = conversionFactor;
        this.convertedValue = convertedValue;
        this.environment = environment;
    }

    public Exchange() {}

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id =  UuidCreator.getTimeOrderedEpoch();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public BigDecimal getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(BigDecimal conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public BigDecimal getConvertedValue() {
        return convertedValue;
    }

    public void setConvertedValue(BigDecimal convertedValue) {
        this.convertedValue = convertedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Exchange exchange = (Exchange) o;
        return Objects.equals(id, exchange.id) && Objects.equals(from, exchange.from) && Objects.equals(to, exchange.to) && Objects.equals(environment, exchange.environment) && Objects.equals(conversionFactor, exchange.conversionFactor) && Objects.equals(convertedValue, exchange.convertedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, environment, conversionFactor, convertedValue);
    }
}

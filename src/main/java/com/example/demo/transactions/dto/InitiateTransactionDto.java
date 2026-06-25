    package com.example.demo.transactions.dto;

    import com.example.demo.shared.enums.Currency;
import com.example.demo.shared.enums.Gateway;
import com.example.demo.transactions.entity.TransactionEntity;
    import jakarta.validation.constraints.DecimalMin;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import lombok.Data;

    import java.math.BigDecimal;
    import java.util.Map;

    @Data
    public class InitiateTransactionDto {

        @NotBlank(message = "Wallet ID is required")
        private String walletId;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;

        private Currency currency = Currency.NGN;

        @NotNull(message = "Type is required")
        private TransactionEntity.Type type;

        @NotNull(message = "Gateway is required")
        private Gateway gateway;

        private String description;
        private String externalReference;
        private Map<String, String> metadata;

    }
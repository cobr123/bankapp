package com.example.bankapp.ui;

import com.example.bankapp.ui.configuration.CashClientProperties;
import com.example.bankapp.ui.configuration.ExchangeClientProperties;
import com.example.bankapp.ui.configuration.TransferClientProperties;
import com.example.bankapp.ui.configuration.UserClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({UserClientProperties.class, TransferClientProperties.class, CashClientProperties.class, ExchangeClientProperties.class})
public class UiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}

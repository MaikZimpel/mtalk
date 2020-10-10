package de.esko.dfs.ph.config;

import de.esko.dfs.actor.BusConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AppConfig {

    @Value("${esko.machine.name}")
    private String machineName;
    @Value("${esko.bus.name}")
    private String busName;

    @Bean
    public BusConnector busConnector() {
        return new BusConnector(machineName, busName);
    }
}

package de.esko.dfs.xps.config;

import de.esko.dfs.actor.BusConnector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Value("${esko.machine.name}")
    private String machineName;
    @Value("${esko.bus.name}")
    private String busName;

    @Bean
    public BusConnector busConnector() {
        return new BusConnector(machineName, busName);
    }
}

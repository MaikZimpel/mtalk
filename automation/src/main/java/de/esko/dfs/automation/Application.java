package de.esko.dfs.automation;

import de.esko.dfs.automation.ui.AutomationMonitorUi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.awt.*;

@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String ... args) {
        var ctx = new SpringApplicationBuilder(Application.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .addCommandLineProperties(true)
                .run(args);
        EventQueue.invokeLater(() -> {
            var ui = ctx.getBean(AutomationMonitorUi.class);
            ui.setSize(400, 600);
            ui.setTitle(ui.getName());
            ui.setVisible(true);
        });
    }

}

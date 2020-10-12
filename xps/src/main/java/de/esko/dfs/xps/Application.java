package de.esko.dfs.xps;

import de.esko.dfs.xps.ui.XpsUi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.awt.*;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String ... args) {
        var ctx = new SpringApplicationBuilder(Application.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .addCommandLineProperties(true)
                .run(args);
        EventQueue.invokeLater(() -> {
            var ui = ctx.getBean(XpsUi.class);
            ui.setSize(800, 600);
            ui.setTitle(ui.getName());
            ui.setVisible(true);
        });
    }
}

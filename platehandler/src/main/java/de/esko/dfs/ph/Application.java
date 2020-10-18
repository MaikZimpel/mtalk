package de.esko.dfs.ph;

import de.esko.dfs.ph.ui.PlatehandlerUi;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.awt.*;

@SpringBootApplication
public class Application {

    public static void main(String ... args) {
        var ctx = new SpringApplicationBuilder(Application.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .addCommandLineProperties(true)
                .run(args);
        EventQueue.invokeLater(() -> {
            var ui = ctx.getBean(PlatehandlerUi.class);
            ui.setSize(400, 600);
            ui.setTitle(ui.getName());
            ui.setVisible(true);
        });
    }

}

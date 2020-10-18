package de.esko.dfs.automation.ui;

import de.esko.dfs.actor.BusConnector;
import de.esko.dfs.automation.statemachine.State;
import de.esko.dfs.message.Command;
import de.esko.dfs.message.CommandMessageCodec;
import de.esko.dfs.statemachine.Event;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.spi.cluster.hazelcast.ConfigUtil;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Slf4j
@Component
public class AutomationMonitorUi extends JFrame {
    private final StateMachine<State, Event> stateMachine;
    private final BusConnector actor;

    private final JTextPane display = new JTextPane();
    private final JButton autoSetupBtn = new JButton("Auto Setup");
    private final JButton startProgramBtn = new JButton("Program Start");

    @Value("${esko.machine.name}")
    @Getter
    private String name;

    public AutomationMonitorUi(StateMachine<State, Event> stateMachine, BusConnector actor) {
        super();
        this.stateMachine = stateMachine;
        this.actor = actor;
        var vertxOptions = new VertxOptions();
        vertxOptions.setClusterManager(new HazelcastClusterManager(ConfigUtil.loadConfig()));
        Vertx.clusteredVertx(vertxOptions, res -> {
            if (res.succeeded()) {
                var vrtx = res.result();
                vrtx.deployVerticle(actor);
                Class<Event> type = Event.class;
                vrtx.eventBus().registerDefaultCodec(Command.class, new CommandMessageCodec());
                actor.receive(this::onMessage);
            }
        });

        autoSetupBtn.addActionListener((e) -> actor.send(new Command(Event.AUTO_SETUP.value(), "AUTO SETUP", name, Event.AUTO_SETUP.name())));
        startProgramBtn.addActionListener((e) -> actor.send(new Command(Event.AUTO_START.value(), "Starte Program", name, Event.AUTO_START.name())));
        JButton autoResetBtn = new JButton("Auto Reset");
        autoResetBtn.addActionListener((e) -> actor.send(new Command(Event.AUTO_RESET.value(), "Reset all", name, Event.AUTO_RESET.name())));
        stateMachine.addStateListener(new AutomationStatemachineListener(this, actor));

        final JPanel mainPanel = new JPanel();
        mainPanel.add(autoSetupBtn);
        mainPanel.add(startProgramBtn);
        mainPanel.add(display);
        startProgramBtn.setEnabled(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
    }

    private void onMessage(Message<Command> commandMessage) {
        var command = commandMessage.body();
        Event event = Event.valueOf(command.getEvent());
        if (event != null) {
            if (!command.getOrigin().equals(name)) {
                addToDisplay("received: " + event.name() + " from " + command.getOrigin());
            }
            stateMachine.sendEvent(event);
        }
    }

    public void addToDisplay(String text) {
        display.setText(display.getText() + "\n" + text);
    }

    public void setAutoSetupBtnEnabled(boolean parseBoolean) {
        autoSetupBtn.setEnabled(parseBoolean);
    }

    public void setStartBtnEnabled(boolean b) {
        startProgramBtn.setEnabled(b);
    }

    public void busyState() {
        autoSetupBtn.setEnabled(false);
        startProgramBtn.setEnabled(false);
    }

    public void readyState() {
        autoSetupBtn.setEnabled(true);
        startProgramBtn.setEnabled(false);
    }
}

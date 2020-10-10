package de.esko.dfs.ph.ui;

import de.esko.dfs.actor.BusConnector;
import de.esko.dfs.message.Command;
import de.esko.dfs.message.CommandMessageCodec;
import de.esko.dfs.ph.statemachine.Event;
import de.esko.dfs.ph.statemachine.State;
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

@Component
@Slf4j
public class PlatehandlerUi extends JFrame {

    private final StateMachine<State, Event> stateMachine;

    private final JTextPane display = new JTextPane();
    private final JButton command101Button = new JButton("Load CDI");
    private final JButton command102Button = new JButton("Unload CDI");
    private final JButton command103Button = new JButton("Reset");
    private final JButton command104Button = new JButton("Panic");

    @Value("${esko.machine.name}")
    @Getter
    private String name;

    public PlatehandlerUi(StateMachine<State, Event> stateMachine, BusConnector actor) {
        super();
        this.stateMachine = stateMachine;
        stateMachine.start();
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

        command101Button.addActionListener((e) -> actor.send(new Command(401, "Load CDI", name, Event.PH_LOAD_CDI.name())));
        command102Button.addActionListener((e) -> actor.send(new Command(402, "Unload CDI", name, Event.PH_UNLOAD_CDI.name())));
        command103Button.addActionListener((e) -> actor.send(new Command(404, "RESET", name, Event.PH_RESET.name())));
        command104Button.addActionListener((e) -> actor.send(new Command(405, "ABORT", name,  Event.PH_ABORT.name())));
        stateMachine.addStateListener(new PlatehandlerStateMachineListener(this));

        final JPanel mainPanel = new JPanel();
        mainPanel.add(command101Button);
        mainPanel.add(command102Button);
        mainPanel.add(command103Button);
        mainPanel.add(command104Button);
        mainPanel.add(display);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
    }

    private void onMessage(Message<Command> commandMessage) {
        if (Event.valueOf(commandMessage.body().getEvent()) == Event.GLOBAL_ACK) {
            log.info("ACK received from " + commandMessage.body().getOrigin());
            return;
        }
        if (commandMessage.headers().get(BusConnector.NAME_KEY).equals(name)) {
            log.info("[" + name + "]: ignore self message.");
        } else {
            var command = commandMessage.body();
            display.setText(display.getText() + "\n" + "received: " + commandMessage.body() + " from " + command.getOrigin());
            commandMessage.reply(new Command(999, "ACK", name, Event.GLOBAL_ACK.name()));
            stateMachine.sendEvent(Event.valueOf(command.getEvent()));
        }

    }

    void disable101() {
        enableButtons();
        command101Button.setEnabled(false);
    }

    void disable102() {
        enableButtons();
        command102Button.setEnabled(false);
    }

    void disable103() {
        enableButtons();
        command103Button.setEnabled(false);
    }

    void disable104() {
        enableButtons();
        command104Button.setEnabled(false);
    }

    void enableButtons() {
        command101Button.setEnabled(true);
        command102Button.setEnabled(true);
        command103Button.setEnabled(true);
        command104Button.setEnabled(true);
    }
}

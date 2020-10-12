package de.esko.dfs.xps.ui;

import de.esko.dfs.actor.BusConnector;
import de.esko.dfs.message.Command;
import de.esko.dfs.message.CommandMessageCodec;
import de.esko.dfs.statemachine.Event;
import de.esko.dfs.xps.statemachine.State;
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
public class XpsUi extends JFrame {

    private final StateMachine<State, Event> stateMachine;

    private final JTextPane display = new JTextPane();
    private final JButton s1Btn = new JButton("S1");
    private final JButton s2Btn = new JButton("S2");
    private final JButton resetBtn = new JButton("Reset");
    private final JButton panicBtn = new JButton("Panic");

    @Value("${esko.machine.name}")
    @Getter
    private String name;

    public XpsUi(StateMachine<State, Event> stateMachine, BusConnector actor) {
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

        s1Btn.addActionListener((e) -> actor.send(new Command(501, "XPS S1", name, Event.XPS_S1)));
        s2Btn.addActionListener((e) -> actor.send(new Command(502, "XPS S2", name, Event.XPS_S2)));
        resetBtn.addActionListener((e) -> actor.send(new Command(504, "RESET", name, Event.XPS_RESET)));
        panicBtn.addActionListener((e) -> actor.send(new Command(505, "ABORT", name, Event.XPS_ABORT)));
        stateMachine.addStateListener(new XpsStateMachineListener(this));

        final JPanel mainPanel = new JPanel();
        mainPanel.add(s1Btn);
        mainPanel.add(s2Btn);
        mainPanel.add(resetBtn);
        mainPanel.add(panicBtn);
        mainPanel.add(display);

        s1Btn.setEnabled(false);
        s2Btn.setEnabled(false);
        resetBtn.setEnabled(false);
        panicBtn.setEnabled(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
    }

    private void onMessage(Message<Command> commandMessage) {
        var command = commandMessage.body();
        display.setText(display.getText() + "\n" + "received: " + commandMessage.body() + " from " + command.getOrigin());
        if (command.getEvent() == Event.GLOBAL_RDY) {
            if (command.getOrigin().equals(name)) {
                stateMachine.sendEvent(command.getEvent());
            }
        } else {
            stateMachine.sendEvent(command.getEvent());
        }
    }

    void mainOnState() {
        log.debug("ENTER MAIN ON STATE");
        s1Btn.setEnabled(true);
        s2Btn.setEnabled(true);
        resetBtn.setEnabled(false);
        panicBtn.setEnabled(true);
    }

    void busyState() {
        s1Btn.setEnabled(false);
        s2Btn.setEnabled(false);
        resetBtn.setEnabled(true);
        panicBtn.setEnabled(true);
    }

    public void errorState() {
        s1Btn.setEnabled(false);
        s2Btn.setEnabled(false);
        resetBtn.setEnabled(true);
        panicBtn.setEnabled(false);
    }
}

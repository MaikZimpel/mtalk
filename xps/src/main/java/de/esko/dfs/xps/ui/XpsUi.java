package de.esko.dfs.xps.ui;

import de.esko.dfs.actor.BusConnector;
import de.esko.dfs.message.Command;
import de.esko.dfs.message.CommandMessageCodec;
import de.esko.dfs.statemachine.Event;
import de.esko.dfs.xps.statemachine.State;
import de.esko.dfs.xps.statemachine.XpsEvent;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class XpsUi extends JFrame {

    private final StateMachine<State, Event> stateMachine;
    private final BusConnector actor;

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
        this.actor = actor;
        var vertxOptions = new VertxOptions();
        vertxOptions.setClusterManager(new HazelcastClusterManager(ConfigUtil.loadConfig()));
        Vertx.clusteredVertx(vertxOptions, res -> {
            if (res.succeeded()) {
                var vrtx = res.result();
                vrtx.deployVerticle(actor);
                vrtx.eventBus().registerDefaultCodec(Command.class, new CommandMessageCodec());
                actor.receive(this::onMessage);
            }
        });

        s1Btn.addActionListener((e) -> actor.send(new Command(XpsEvent.XPS_S1.value(), "XPS S1", name, XpsEvent.XPS_S1.name())));
        s2Btn.addActionListener((e) -> actor.send(new Command(XpsEvent.XPS_S2.value(), "XPS S2", name, XpsEvent.XPS_S2.name())));
        resetBtn.addActionListener((e) -> actor.send(new Command(XpsEvent.XPS_RESET.value(), "RESET", name, XpsEvent.XPS_RESET.name())));
        panicBtn.addActionListener((e) -> actor.send(new Command(XpsEvent.XPS_ABORT.value(), "ABORT", name, XpsEvent.XPS_ABORT.name())));
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
        Event event = Event.valueOf(command.getEvent());
        if (event != null) {
            if (!command.getOrigin().equals(name)) {
                addToDisplay("received: " + commandMessage.body().getDescription() + " from " + command.getOrigin());
            }
            stateMachine.sendEvent(event);
        }
    }

    public void addToDisplay(String text) {
        display.setText(display.getText() + "\n" + text);
    }

    public void sendCommandInXSeconds(Command command, int secondsDelay) {
        var f = new CompletableFuture<>();
        f.completeAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(secondsDelay);
            } catch (InterruptedException e) {
                log.error("Thread interrupted.", e);
            }
            return null;
        }).thenRun(() -> actor.send(command));
    }

    void mainOnState() {
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

package de.esko.dfs.ph.ui;

import de.esko.dfs.actor.BusConnector;
import de.esko.dfs.message.Command;
import de.esko.dfs.message.CommandMessageCodec;
import de.esko.dfs.ph.statemachine.State;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

@Component
@Slf4j
public class PlatehandlerUi extends JFrame {

    private final StateMachine<State, Event> stateMachine;

    private final JTextPane display = new JTextPane();
    private final JButton loadCdiBtn = new JButton("Load CDI");
    private final JButton unloadCdiBtn = new JButton("Unload CDI");
    private final JButton resetBtn = new JButton("Reset");
    private final JButton panicBtn = new JButton("Panic");

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

        loadCdiBtn.addActionListener((e) -> actor.send(new Command(401, "Load CDI", name, Event.PH_LOAD_CDI)));
        unloadCdiBtn.addActionListener((e) -> actor.send(new Command(402, "Unload CDI", name, Event.PH_UNLOAD_CDI)));
        resetBtn.addActionListener((e) -> actor.send(new Command(404, "RESET", name, Event.PH_RESET)));
        panicBtn.addActionListener((e) -> actor.send(new Command(405, "ABORT", name, Event.PH_ABORT)));
        stateMachine.addStateListener(new PlatehandlerStateMachineListener(this));

        final JPanel mainPanel = new JPanel();
        mainPanel.add(loadCdiBtn);
        mainPanel.add(unloadCdiBtn);
        mainPanel.add(resetBtn);
        mainPanel.add(panicBtn);
        mainPanel.add(display);

        loadCdiBtn.setEnabled(false);
        unloadCdiBtn.setEnabled(false);
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
        loadCdiBtn.setEnabled(true);
        unloadCdiBtn.setEnabled(true);
        resetBtn.setEnabled(false);
        panicBtn.setEnabled(true);
    }

    void loadToCdiState() {
        loadCdiBtn.setEnabled(false);
        unloadCdiBtn.setEnabled(false);
        resetBtn.setEnabled(true);
        panicBtn.setEnabled(true);
    }

    void loadFromCdiState() {
        loadCdiBtn.setEnabled(false);
        unloadCdiBtn.setEnabled(false);
        resetBtn.setEnabled(true);
        panicBtn.setEnabled(true);
    }

    public void errorState() {
        loadCdiBtn.setEnabled(false);
        unloadCdiBtn.setEnabled(false);
        resetBtn.setEnabled(true);
        panicBtn.setEnabled(false);
    }
}

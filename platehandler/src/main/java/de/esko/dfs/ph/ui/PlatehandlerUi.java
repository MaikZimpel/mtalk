package de.esko.dfs.ph.ui;

import de.esko.dfs.actor.BusConnector;
import de.esko.dfs.message.Command;
import de.esko.dfs.message.CommandMessageCodec;
import de.esko.dfs.ph.statemachine.PhEvent;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PlatehandlerUi extends JFrame {

    private final StateMachine<State, Event> stateMachine;
    private final BusConnector actor;

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

        loadCdiBtn.addActionListener((e) -> actor.send(new Command(PhEvent.PH_LOAD_CDI.value(), "Load CDI", name, PhEvent.PH_LOAD_CDI.name())));
        unloadCdiBtn.addActionListener((e) -> actor.send(new Command(PhEvent.PH_UNLOAD_CDI.value(), "Unload CDI", name, PhEvent.PH_UNLOAD_CDI.name())));
        resetBtn.addActionListener((e) -> actor.send(new Command(PhEvent.PH_RESET.value(), "RESET", name, PhEvent.PH_RESET.name())));
        panicBtn.addActionListener((e) -> actor.send(new Command(PhEvent.PH_ABORT.value(), "ABORT", name, PhEvent.PH_ABORT.name())));
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
        sendCommandDelayed(new Command(PhEvent.PH_RESET.value(), "RESET", name, PhEvent.PH_RESET.name()), 10);
    }

    void rcState() {
        loadCdiBtn.setEnabled(false);
        unloadCdiBtn.setEnabled(false);
        resetBtn.setEnabled(false);
        panicBtn.setEnabled(true);
    }

    public void sendCommandDelayed(Command command, int secondsDelay) {
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

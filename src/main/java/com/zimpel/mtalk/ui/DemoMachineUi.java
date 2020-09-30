package com.zimpel.mtalk.ui;

import com.zimpel.mtalk.actor.ActorVerticle;
import com.zimpel.mtalk.message.Command;
import com.zimpel.mtalk.message.CommandMessageCodec;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.spi.cluster.ClusterManager;

import javax.swing.*;

public class DemoMachineUi extends JFrame {

    private final JTextPane display = new JTextPane();
    private final String name;
    private ActorVerticle actor;
    private final JButton command101Button = new JButton("CMD 101");
    private final JButton command102Button = new JButton("CMD 102");
    private final JButton command103Button = new JButton("CMD 103");
    private final JButton command104Button = new JButton("CMD 104");

    public DemoMachineUi(String title) {
        super(title);
        actor = new ActorVerticle(title);
        var vrtxCnfig = new VertxOptions();
        var evntBusOptions = new EventBusOptions();
        evntBusOptions.setClustered(true);
        evntBusOptions.setClusterPublicHost("192.168.178.43");
        evntBusOptions.setClusterPublicPort(5701);
        vrtxCnfig.setEventBusOptions(evntBusOptions);
        Vertx.clusteredVertx(vrtxCnfig, res -> {
            if (res.succeeded()) {
                var vrtx = res.result();
                vrtx.deployVerticle(actor);
                vrtx.eventBus().registerDefaultCodec(Command.class, new CommandMessageCodec());
                actor.receive(this::appendToDisplay);
            }
        });
        this.name = title;

        command101Button.addActionListener((event) -> {
            actor.send(new Command(101, "101", title));
        });


        command102Button.addActionListener((event) -> {
            actor.send(new Command(102, "102", title));
        });


        command103Button.addActionListener((event) -> {
            actor.send(new Command(103, "103", title));
        });


        command104Button.addActionListener((event) -> {
            actor.send(new Command(104, "104", title));
        });

        JPanel mainPanel = new JPanel();
        mainPanel.add(command101Button);
        mainPanel.add(command102Button);
        mainPanel.add(command103Button);
        mainPanel.add(command104Button);
        mainPanel.add(display);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
    }

    private void appendToDisplay(Message<Command> commandMessage) {
        if (commandMessage.headers().get(ActorVerticle.NAME_KEY).equals(name)) {
            System.out.println("[" + name + "]: ignore self message.");
        } else {
            var command = commandMessage.body();
            display.setText(display.getText() + "\n" + "received: " + commandMessage.body() + " from " + command.getMid());
            switch (command.getCode()) {
                case 900: {actor.send(new Command(901, "ACKRDY " + command.getMid(), name)); break;}
                case 101: {disable101();break;}
                case 102: {disable102();break;}
                case 103: {disable103();break;}
                case 104: {disable104();break;}
                default:
            }
        }
    }

    private void disable101() {
        enableButtons();
        command101Button.setEnabled(false);
    }

    private void disable102() {
        enableButtons();
        command102Button.setEnabled(false);
    }

    private void disable103() {
        enableButtons();
        command103Button.setEnabled(false);
    }

    public void disable104() {
        enableButtons();
        command104Button.setEnabled(false);
    }

    private void enableButtons() {
        command101Button.setEnabled(true);
        command102Button.setEnabled(true);
        command103Button.setEnabled(true);
        command104Button.setEnabled(true);
    }
}

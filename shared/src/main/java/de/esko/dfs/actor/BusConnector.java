package de.esko.dfs.actor;

import de.esko.dfs.message.Command;
import de.esko.dfs.statemachine.Event;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BusConnector extends AbstractVerticle {


    public final static String NAME_KEY = "ACTOR_NAME";

    private final String machineName;
    private final String busName;

    @Override
    public void start(Promise<Void> promise) {
        log.info(machineName + " attached to event bus.");
        send(new Command(999, "Connected to Bus", machineName, Event.GLOBAL_RDY));
        promise.complete();
    }

    public void send(Command command) {
        var deliveryOptions = new DeliveryOptions();
        deliveryOptions.setHeaders(MultiMap.caseInsensitiveMultiMap().add(NAME_KEY, machineName));
        log.debug("[" + machineName + "]: publishing message: " + command);
        vertx.eventBus().publish(busName, command, deliveryOptions);
    }

    public void receive(Handler<Message<Command>> handler)  {
        vertx.eventBus().consumer(busName, handler);
    }
}

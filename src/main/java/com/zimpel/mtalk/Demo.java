package com.zimpel.mtalk;

import com.zimpel.mtalk.actor.ActorVerticle;
import com.zimpel.mtalk.message.Command;
import com.zimpel.mtalk.message.CommandMessageCodec;
import com.zimpel.mtalk.ui.DemoMachineUi;
import io.vertx.core.Vertx;

import java.util.Arrays;

public class Demo {

    public static void main(String ... s) throws Exception {
        Arrays.asList(s).forEach(System.out::println);
        if (s.length < 1) {
            throw new Exception("Configuration Error");
        }
        var iu1 = new DemoMachineUi(s[0]);
        /*var machine2 = new ActorVerticle("Machine 2");
        vertx.deployVerticle(machine2);
        var ui2 = new DemoMachineUi(machine2, "Machine 2");
        var machine3 = new ActorVerticle("Machine 3");
        vertx.deployVerticle(machine3);
        var ui3 = new DemoMachineUi(machine3, "Machine 3");*/
        iu1.setSize(640,480);
        iu1.setVisible(true);
       /* ui2.setVisible(true);
        ui2.setSize(400, 300);
        ui2.setLocation(iu1.getX() + 400, iu1.getY());
        ui3.setVisible(true);
        ui3.setSize(400, 300);
        ui3.setLocation(ui2.getX() + 400, ui2.getY());*/
    }
}

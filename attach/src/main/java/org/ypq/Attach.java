package org.ypq;

import com.sun.tools.attach.VirtualMachine;

public class Attach {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("请确保输入两个参数, PID, weave/reset/redefine");
        }
        VirtualMachine vm = VirtualMachine.attach(args[0]);
        vm.loadAgent("agent-1.0-SNAPSHOT.jar", args[1]);
        vm.detach();
    }

}

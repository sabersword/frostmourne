package org.ypq.attach;

import com.sun.tools.attach.VirtualMachine;

public class Attach {

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("请确保输入四个参数, PID, weave/reset/redefine, className, methodName");
        }
        VirtualMachine vm = VirtualMachine.attach(args[0]);
        String agentArg = args[1] + " " + args[2] + " " + args[3];
        vm.loadAgent("agent-1.0-SNAPSHOT.jar", agentArg);
        vm.detach();
    }

}

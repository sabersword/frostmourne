package org.ypq;

import com.sun.tools.attach.VirtualMachine;

public class Attach {

    public static void main(String[] args) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(args[0]);
        vm.loadAgent("agent-1.0-SNAPSHOT.jar", "");
        vm.detach();
    }

}

package org.ypq.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ResetClassFileTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        return null;
    }
}

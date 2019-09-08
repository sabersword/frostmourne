package java.ypq.spy;

import java.lang.reflect.Method;

public class Spy {

    public static volatile Method ON_BEFORE_METHOD;
    public static volatile Method ON_RETURN_METHOD;
    public static volatile Method ON_THROW_METHOD;

    public static void init(
            Method onBeforeMethod,
            Method onReturnMethod,
            Method onThrowMethod) {
        ON_BEFORE_METHOD = onBeforeMethod;
        ON_RETURN_METHOD = onReturnMethod;
        ON_THROW_METHOD = onThrowMethod;
    }

}

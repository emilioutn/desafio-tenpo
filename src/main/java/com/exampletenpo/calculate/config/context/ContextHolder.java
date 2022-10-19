package com.exampletenpo.calculate.config.context;

public class ContextHolder {
    private static final InheritableThreadLocal<Context> context =
            new InheritableThreadLocal<>() {
                public Context initialValue() {
                    return new Context();
                }
            };

    public static Context getContext() {
        return context.get();
    }

    public static void setIpFrom(String ipFrom) {
        getContext().setIpFrom(ipFrom);
    }

    public static void setResponse(String response) {
        getContext().setResponse(response);
    }

    public static void clear() {
        context.remove();
    }
}

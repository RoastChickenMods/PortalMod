package chickendinner.portalmod.util;

import chickendinner.portalmod.PortalMod;

public enum Util {
    ;

    public static void printStackTrace() {
        printStackTrace(0);
    }

    public static void printStackTrace(int toSkip) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length - toSkip; i++) {
            if (i < 2 + toSkip) continue;
            PortalMod.LOGGER.error(stackTrace[i]);
        }
    }

    public static void printErrorWithStackTrace(String error) {
        PortalMod.LOGGER.error(error);
        printStackTrace(1);
    }

}

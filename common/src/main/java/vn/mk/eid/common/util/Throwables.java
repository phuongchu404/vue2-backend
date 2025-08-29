package vn.mk.eid.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Throwables {
    private Throwables() {
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}

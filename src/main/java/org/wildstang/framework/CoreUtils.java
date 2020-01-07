package org.wildstang.framework;

import com.ctre.phoenix.ErrorCode;

public class CoreUtils {
    public static void checkNotNull(Object p_param, String p_message) {
        if (p_param == null) {
            throw new NullPointerException(p_message);
        }
    }

    /** This class wraps CTRE ErrorCodes as exceptions for programming
     * convenience */
    /*public static class CTREException extends RuntimeException {
        static final long serialVersionUID = 1L;
        ErrorCode error;

        public CTREException(ErrorCode error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return Integer.toString(error.value);
        }
    }

    public static void checkCTRE(ErrorCode error) throws CTREException {
        if (error != ErrorCode.OK) {
            throw new CTREException(error);
        }
    }*/
}

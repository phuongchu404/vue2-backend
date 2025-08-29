package vn.mk.eid.web.constant;

import lombok.experimental.UtilityClass;

public class WebConstants {
    @UtilityClass
    public class CommonSymbol {

        public final String SPACE = " ";

        public final String DOT = ".";

        public final String BACKSLASH = "\\";

        public final String COMMA = ",";

        public final String DASH = "-";

        public final String SHIFT_DASH = "_";

        public final String COLON = ":";

        public final String FORWARD_SLASH = "/";
    }

    @UtilityClass
    public class bucketMinio {
        public final String DETAINEE = "detainee";
        public final String IDENTITY = "identity";
        public final String FINGERPRINT = "fingerprint";
        public final String IDENTITY_PREFIX = "identity-";
        public final String FINGERPRINT_PREFIX = "fingerprint-";

    }

    @UtilityClass
    public class IdentityRecord {
        public final String IDENTITY_RECORD_PREFIX = "identity_";
    }

    @UtilityClass
    public class Fingerprint {
        public final String FINGERPRINT_PREFIX = "fingerprint_";
    }
}

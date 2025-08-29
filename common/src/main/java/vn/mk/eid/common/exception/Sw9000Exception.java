package vn.mk.eid.common.exception;

public class Sw9000Exception extends Exception {
    public Sw9000Exception() {
        super();
    }

    public Sw9000Exception(String errorMessage) {
        super(errorMessage);
    }

    public Sw9000Exception(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}

package net.junhabaek.authserver.common.exception;

public class BusinessException extends RuntimeException{
    private final ErrorStatus errorStatus;

    protected BusinessException(String message, ErrorStatus errorStatus){
        super(message);
        this.errorStatus=errorStatus;
    }

    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }
}

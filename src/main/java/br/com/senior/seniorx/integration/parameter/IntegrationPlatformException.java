package br.com.senior.seniorx.integration.parameter;

public class IntegrationPlatformException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IntegrationPlatformException(String message) {
        super(message);
    }

    public IntegrationPlatformException(Throwable cause) {
        super(cause);
    }

}

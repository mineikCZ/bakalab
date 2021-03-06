package org.bakalab.app.items;


public class LoginResponse {

    private boolean success;
    private String errorMessage = null;
    private String[] successResponse = null;


    public LoginResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public LoginResponse(boolean success, String[] successResponse) {
        this.success = success;
        this.successResponse = successResponse;
    }

    public boolean wasSuccessful() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String[] getSuccessResponse() {
        return successResponse;
    }

}

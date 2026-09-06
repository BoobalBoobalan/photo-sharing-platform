package com.photoshare.platform.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PinVerificationRequest {
    @NotBlank(message = "PIN is required")
    private String pin;

    public PinVerificationRequest() {}

    public PinVerificationRequest(String pin) {
        this.pin = pin;
    }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}

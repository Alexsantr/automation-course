package model.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpResponse {
    private String otp_code;
    private String message;
    private Integer expires_in;
}
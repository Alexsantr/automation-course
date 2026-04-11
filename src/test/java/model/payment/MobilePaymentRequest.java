package model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobilePaymentRequest {
    public int account_id;
    public String operator;
    public String phone;
    public String amount;
    public String otp_code;
}
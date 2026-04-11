package model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorPaymentRequest {
    public int account_id;
    public String provider;
    public String account_number;
    public String amount;
    public String otp_code;
}
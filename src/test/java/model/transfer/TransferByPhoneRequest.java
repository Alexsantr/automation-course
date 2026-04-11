package model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferByPhoneRequest {
    public int from_account_id;
    public String phone;
    public String amount;
    public String recipient_bank_id;
    public String otp_code;
}
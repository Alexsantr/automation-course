package model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRequest {
    public int from_account_id;
    public int to_account_id;
    public String amount;
    public String otp_code;
}
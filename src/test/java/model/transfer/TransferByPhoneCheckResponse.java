package model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferByPhoneCheckResponse {
    public boolean inOurBank;
    public List<BankOption> availableBanks;
}
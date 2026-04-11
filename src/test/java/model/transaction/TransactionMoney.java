package model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionMoney {
    public String amount;
    public String fee;
    public String total;
    public String currency;
}
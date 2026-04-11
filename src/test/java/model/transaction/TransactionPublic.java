package model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionPublic {
    public int id;
    public String type;
    public TransactionMoney money;
    public String description;
    public String created_at;
    public Integer from_account_id;
    public Integer to_account_id;
    public String status;
}
package model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountPublic {
    public int id;
    public String account_number;
    public String account_type;
    public String currency;
    public String balance;
    public boolean is_primary;
}
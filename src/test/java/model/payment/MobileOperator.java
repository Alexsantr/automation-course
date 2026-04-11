package model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileOperator {
    public String id;
    public String label;
    public String regex;
}
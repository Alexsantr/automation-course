package model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRatesResponse {
    public Map<String, Double> rates;
    public String updated_at;
}
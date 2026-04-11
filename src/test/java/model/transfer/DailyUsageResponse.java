package model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyUsageResponse {
    public Map<String, DailyLimit> limits;

    @Data
    public static class DailyLimit {
        public double used;
        public double limit;
        public String currency;
    }
}
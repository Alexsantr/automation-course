package model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    public String detail;
    public List<ValidationError> errors;

    @Data
    public static class ValidationError {
        public List<Object> loc;
        public String msg;
        public String type;
    }
}
package model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private String detail;
    private List<ValidationError> errors;

    @Data
    public static class ValidationError {
        private List<Object> loc;
        private String msg;
        private String type;
    }
}
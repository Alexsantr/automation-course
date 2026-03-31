package model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class AuthResponse {
    public String access_token;
    public String role;
    public String token_type;
}

package model.auth;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RegisterResponse {
    public int id;
    public String login;
    public String email;
    public String role;
    public String status;
    public String first_name;
    public String last_name;
    public String phone;
}

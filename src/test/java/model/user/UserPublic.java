package model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPublic {
    public int id;
    public String login;
    public String email;
    public String role;
    public String status;
    public String first_name;
    public String last_name;
    public String phone;
}
package model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileUpdateRequest {
    public String first_name;
    public String last_name;
    public String phone;
    public String email;
    public String current_password;
    public String new_password;
}
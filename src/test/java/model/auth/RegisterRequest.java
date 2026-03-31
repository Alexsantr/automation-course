package model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
@JsonIgnoreProperties
@Data // Генерирует геттеры, сеттеры, toString, equals, hashCode
@Builder // Позволяет удобно создавать объекты
@Jacksonized
public class RegisterRequest {
    public String login;
    public String password;
}

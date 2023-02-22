package onuroztnc.userservice.Payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Boolean success;
    private String message;
    private Long userId;
    private String accessToken;
    private String tokenType = "Bearer";

    public LoginResponse(boolean success, String message, Long userId, String jwt) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.accessToken = jwt;
    }
}

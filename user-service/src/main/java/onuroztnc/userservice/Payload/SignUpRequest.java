package onuroztnc.userservice.Payload;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    private String name;
    private String username;
    private String email;
    private String password;

}
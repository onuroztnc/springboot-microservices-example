package onuroztnc.logservice.Payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogResponse {
    private String username;
    private String productName;
    private Long userId;
    private Long productId;
    private Date createdAt;
}
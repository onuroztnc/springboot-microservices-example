package onuroztnc.logservice.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(value = "log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Log {
    @Id
    private String id;
    private String username;
    private String productName;
    private Long userId;
    private Long productId;
    private Date createdAt;
}

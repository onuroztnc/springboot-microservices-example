package onuroztnc.productservice.Payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import onuroztnc.productservice.Dto.ProductDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Boolean success;
    private String message;
    private List<ProductDto> productList;
}


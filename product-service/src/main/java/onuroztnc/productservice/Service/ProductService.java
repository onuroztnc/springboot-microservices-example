package onuroztnc.productservice.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import onuroztnc.productservice.Dto.ProductDto;
import onuroztnc.productservice.Exception.UserNotFoundException;
import onuroztnc.productservice.Model.Product;
import onuroztnc.productservice.Payload.LogRequest;
import onuroztnc.productservice.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.jwtSecret}")
    private String secret;

    private int taxRate = 18;
    public void saveProduct(HttpServletRequest request, Long userId, ProductDto productDto) {
        String token = getTokenFromHttpRequest(request);
        Boolean isUserValid = checkUserValid(userId, token);
        if ( isUserValid )
        {
            Product product = mapToProduct(productDto);
            product.setOwner(userId);
            productRepository.save(product);
        }
        else {
            throw  new UserNotFoundException("The user is not exist in system.");
        }
    }

    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductDto).toList();
    }

    public List<ProductDto> getAllProductByUserId(HttpServletRequest request, Long userId) {
        String token = getTokenFromHttpRequest(request);
        Boolean isUserValid = checkUserValid(userId, token);
        if ( isUserValid )
        {
            List<Product> products = productRepository.findByOwner(userId);
            return products.stream().map(this::mapToProductDto).toList();
        }
        else {
            throw  new UserNotFoundException("The user is not exist in system.");
        }

    }

    public boolean updateProduct(Long userId, ProductDto productDto) {
        boolean isOwnerProduct = checkProductOwner(productDto.getId(), userId);
        if ( isOwnerProduct )
        {
            Product product = mapToProduct(productDto);
            product.setOwner(userId);
            productRepository.save(product);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean deleteProduct(Long userId, Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if ( optionalProduct.isPresent() && optionalProduct.get().getOwner().equals(userId))
        {
            productRepository.deleteById(productId);
            return true;
        }
        else
        {
            return false;
        }
    }

    public BigDecimal calculateTax(HttpServletRequest request, Long userId, Long productId) {
        BigDecimal tax;
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if ( optionalProduct.isPresent() && optionalProduct.get().getOwner().equals(userId))
        {
            Product product = optionalProduct.get();
            if ( product.getIsTaxIncluded() )
            {
                // price - (price / 118 * 100 )
                tax = BigDecimal.valueOf(product.getPrice().floatValue() -(product.getPrice().floatValue() / 118.0 * 100.0 ));
            }
            else
            {
                // price * 0.18
                tax = BigDecimal.valueOf(product.getPrice().floatValue() *  0.18);
            }
            String token = getTokenFromHttpRequest(request);
            logProductService(token, userId, product);
            return tax;
        }
        else
        {
            throw  new UserNotFoundException("The user is not exist in system.");
        }
    }

    private Product mapToProduct(ProductDto productDto) {
        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .isTaxIncluded(productDto.getIsTaxIncluded())
                .build();
    }

    private ProductDto mapToProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .isTaxIncluded(product.getIsTaxIncluded())
                .owner(product.getOwner())
                .build();
    }

    private boolean checkProductOwner(Long productId, Long owner)
    {
        Optional<Product> productOptional = productRepository.findById(productId);
        if ( productOptional.isPresent() && productOptional.get().getOwner().equals(owner))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean checkUserValid(Long userId, String token)
    {
        return webClientBuilder.build().get()
                .uri("http://user-service/api/user/isUserValid/"+userId.toString())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    private String getTokenFromHttpRequest(HttpServletRequest request)
    {
        String jwtToken = null;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);

        }
        return jwtToken;
    }

    private void logProductService(String token, Long userId, Product product)
    {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        String username = claims.getSubject();

        LogRequest logRequest = LogRequest.builder()
                .username(username)
                .userId(userId)
                .productId(product.getId())
                .productName(product.getName())
                .createdAt(Calendar.getInstance().getTime())
                .build();

        webClientBuilder.build().post()
                .uri("http://log-service/api/log/createLog")
                .headers(h -> h.setBearerAuth(token))
                .body(Mono.just(logRequest), LogRequest.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}

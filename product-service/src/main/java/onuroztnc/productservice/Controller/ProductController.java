package onuroztnc.productservice.Controller;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import onuroztnc.productservice.Dto.ProductDto;
import onuroztnc.productservice.Exception.UserNotFoundException;
import onuroztnc.productservice.Payload.ProductResponse;
import onuroztnc.productservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Value("${app.jwtSecret}")
    private String secret;

    @PostMapping("/saveProduct")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> saveProduct(HttpServletRequest request, @RequestBody ProductDto productDto) {
        try{
            Long userId = getUserIdFromHtppRequest(request);
            productService.saveProduct(request, userId, productDto);
            List<ProductDto> productDtoList = productService.getAllProductByUserId(request, userId);
            return new ResponseEntity(new ProductResponse(true, "The products have been successfully created.", productDtoList ),
                    HttpStatus.OK);
        }
        catch (UserNotFoundException ex)
        {
            return ResponseEntity.badRequest().body("The user not found.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllProduct")
    public ResponseEntity<?> getAllProducts() {
        List<ProductDto> productDtoList = productService.getAllProducts();
        return new ResponseEntity(new ProductResponse(true, "The products have been successfully received.", productDtoList ),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getAllProductByUserId")
    public ResponseEntity<?> getAllProductByUserId(HttpServletRequest request) {
        try{
            Long userId = getUserIdFromHtppRequest(request);
            List<ProductDto> productDtoList = productService.getAllProductByUserId(request, userId);
            return new ResponseEntity(new ProductResponse(true, "The products have been successfully received.", productDtoList ),
                    HttpStatus.OK);
        }
        catch (UserNotFoundException ex)
        {
            return ResponseEntity.badRequest().body("The user not found.");
        }

    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/calculateTax/{productId}")
    public ResponseEntity<?> calculateTax(HttpServletRequest request, @PathVariable("productId") String productId) {
        try{
            Long userId = getUserIdFromHtppRequest(request);
            BigDecimal tax = productService.calculateTax(request, userId, Long.valueOf(productId));
            return ResponseEntity.ok(tax);
        }
        catch (UserNotFoundException ex)
        {
            return ResponseEntity.badRequest().body("The user not found.");
        }

    }

    @PutMapping("/updateProduct")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateProduct(HttpServletRequest request, @RequestBody ProductDto productDto) {

        Long userId = getUserIdFromHtppRequest(request);
        boolean isSucceeded = productService.updateProduct(userId, productDto);
        if (isSucceeded)
        {
            return ResponseEntity.ok("The product has been successfully updated.");
        }
        else
        {
            return ResponseEntity.badRequest().body("The product has not been successfully updated.");
        }
    }

    @DeleteMapping("/deleteProduct/{productId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteProduct(HttpServletRequest request, @PathVariable("productId") String productId) {
        Long userId = getUserIdFromHtppRequest(request);
        boolean isSucceeded = productService.deleteProduct(userId, Long.valueOf(productId));
        if (isSucceeded)
        {
            return ResponseEntity.ok("The product has been successfully deleted.");
        }
        else
        {
            return ResponseEntity.badRequest().body("The product has not been successfully deleted.");
        }
    }

    private Long getUserIdFromHtppRequest(HttpServletRequest request)
    {
        Long userId = Long.valueOf(-1);
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken).getBody();
            userId = claims.get("user_id", Long.class);
        }
        return userId;
    }


}


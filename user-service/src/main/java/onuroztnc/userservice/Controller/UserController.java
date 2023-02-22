package onuroztnc.userservice.Controller;


import lombok.RequiredArgsConstructor;
import onuroztnc.userservice.Model.ERole;
import onuroztnc.userservice.Model.Role;
import onuroztnc.userservice.Model.User;
import onuroztnc.userservice.Payload.LoginRequest;
import onuroztnc.userservice.Payload.LoginResponse;
import onuroztnc.userservice.Payload.SignUpRequest;
import onuroztnc.userservice.Repository.RoleRepository;
import onuroztnc.userservice.Repository.UserRepository;
import onuroztnc.userservice.Security.jwt.JwtUtils;
import onuroztnc.userservice.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;


    @GetMapping("/isUserValid/{userId}")
    public Boolean isUserValid(@PathVariable("userId") String id) {
        Optional<User> userOptional = userRepository.findById(Long.valueOf(id));
        if ( userOptional.isPresent() )
            return true;
        else
            return false;
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            User user = userService.getUserIdByUsername(loginRequest.getUsernameOrEmail());

            return ResponseEntity.ok(new LoginResponse(
                    true,
                    "The user successfully logged in",
                    user.getId(),
                    jwt));

        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Failed to log in user");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if(userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("The username already exists in the system");
        }

        if(userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("The email already exists in the system");
        }

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
        roles.add(userRole);

        // Creating user's account
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .name(signUpRequest.getName())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .email(signUpRequest.getEmail())
                .roles(roles)
                .build();

        User result = userRepository.save(user);

        List<User> userList = userService.getAllUser();
        return ResponseEntity.ok("The user successfully created");
    }


}
package onuroztnc.userservice.Service;

import lombok.RequiredArgsConstructor;
import onuroztnc.userservice.Model.User;
import onuroztnc.userservice.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder;

    public Boolean existsByUsername(String username) {
        List<User> userList = userRepository.findAll();
        for (User user: userList)
        {
            if ( username.equals(user.getUsername()) )
            {
                return true;
            }
        }
        return false;
    }

    public Boolean existsByEmail(String email) {
        List<User> userList = userRepository.findAll();
        for (User user: userList)
        {
            if ( email.equals(user.getEmail()) )
            {
                return true;
            }
        }
        return false;
    }

    public User getUserIdByUsername(String username)
    {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if ( userOptional.isPresent() )
        {
            return userOptional.get();
        }
        else
        {
            return null;
        }
    }

    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }
}

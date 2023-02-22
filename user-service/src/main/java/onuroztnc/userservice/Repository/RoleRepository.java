package onuroztnc.userservice.Repository;

import onuroztnc.userservice.Model.ERole;
import onuroztnc.userservice.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}

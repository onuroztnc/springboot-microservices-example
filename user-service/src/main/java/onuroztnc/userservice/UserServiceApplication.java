package onuroztnc.userservice;

import onuroztnc.userservice.Model.ERole;
import onuroztnc.userservice.Model.Role;
import onuroztnc.userservice.Repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(RoleRepository roleRepository) {
		return args -> {
			Role admin = new Role();
			admin.setName(ERole.ROLE_ADMIN);

			Role user = new Role();
			admin.setName(ERole.ROLE_USER);

			roleRepository.save(admin);
			roleRepository.save(user);
		};
	}
}

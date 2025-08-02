package hexlet.code.initializer;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "hexlet@example.com";
        String adminPassword = "qwerty";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);
        } else {
            log.info("The user is already exist: {}", adminEmail);
        }
    }
}

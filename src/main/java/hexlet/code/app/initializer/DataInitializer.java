package hexlet.code.app.initializer;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static java.rmi.server.LogStream.log;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    
    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "hexlet@example.com";
        String adminPassword = "qwerty";
        
        if(userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            
            userRepository.save(admin);

        } else {
            LOGGER.info("The user is already exist: {}", adminEmail);
        }
    }
}

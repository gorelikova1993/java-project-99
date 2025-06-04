package hexlet.code.app.initializer;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DataInitializer implements CommandLineRunner {
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "hexlet@example.com";
        String adminPassword = "qwerty";
        
        if(userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            
            userRepository.save(admin);
            System.out.println("Admin was created: " + admin.getEmail());
        } else {
            System.out.println("The user is already exist: " + adminEmail);
        }
    }
}

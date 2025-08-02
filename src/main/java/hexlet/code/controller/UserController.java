package hexlet.code.controller;

import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private UserUtils userUtils;
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        
        var users = userRepository.findAll().stream().map(userMapper::toDto).toList();
        
        var headers = new org.springframework.http.HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(users.size()));
        headers.add("Access-Control-Expose-Headers", "X-Total-Count");
        
        return ResponseEntity.ok().headers(headers).body(users);
    }
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        var user = userMapper.toEntity(dto, passwordEncoder);
        user = userRepository.save(user);
        return ResponseEntity.status(201).body(userMapper.toDto(user));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto data) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        userMapper.updateEntityFromDto(data, user);
        userRepository.save(user);
        var userDTO = userMapper.toDto(user);
        return ResponseEntity.ok(userDTO);
    }
    // Удалить пользователя
    @PreAuthorize("@userUtils.isAuthor(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

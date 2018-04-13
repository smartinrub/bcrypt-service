package org.smartinrub.bcryptservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<User> getUser(@PathVariable("id") Long id) {
        return userRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUser(@Valid @RequestBody User user) {
        user.setPassword(hashPassword(user.getPassword()));
        userRepository.save(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@Valid @RequestBody User user) {

        Optional<User> dbUser = userRepository.findById(user.getId());

        if (!dbUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String hash  = dbUser.get().getPassword();

        if (!BCrypt.checkpw(user.getPassword(), hash)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong Password!");
        }

        return ResponseEntity.ok("Welcome " + user.getEmail());
    }

    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
}

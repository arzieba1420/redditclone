package pl.nazwa.arzieba.redditclone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.nazwa.arzieba.redditclone.dao.UserRepository;
import pl.nazwa.arzieba.redditclone.dao.VerificationTokenRepository;
import pl.nazwa.arzieba.redditclone.dto.RegisterRequest;
import pl.nazwa.arzieba.redditclone.exceptions.SpringRedditException;
import pl.nazwa.arzieba.redditclone.model.NotificationEmail;
import pl.nazwa.arzieba.redditclone.model.User;
import pl.nazwa.arzieba.redditclone.model.VerificationToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private MailService mailService;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, MailService mailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailService = mailService;
    }

    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Active Your Account",
                user.getEmail(),
                "http://localhost:8080/api/auth/accountVerification/"+token));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;


    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()->new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name: " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }
}

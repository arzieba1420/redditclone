package pl.nazwa.arzieba.redditclone.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.nazwa.arzieba.redditclone.model.VerificationToken;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByToken(String token);
}

package pl.nazwa.arzieba.redditclone.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.nazwa.arzieba.redditclone.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}

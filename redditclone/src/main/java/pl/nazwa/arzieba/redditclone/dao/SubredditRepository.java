package pl.nazwa.arzieba.redditclone.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.nazwa.arzieba.redditclone.model.Subreddit;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit,Long> {
}

package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // cerca uno specifico user per email

    Optional<User> findByEmail(String email);

    // cerca uno specifico user per nickname

    Optional<User> findByNickname(String nickname);

    // verifica l'esistenza dell'utente attraverso l'email

    boolean existsByEmail(String email);

    // verifica l'esistenza dell'utente attraverso il nickname

    boolean existsByNickname(String nickname);
}
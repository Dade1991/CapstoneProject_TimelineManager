package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.User_Role;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface User_RoleRepository extends JpaRepository<User_Role, Long> {

    // cerca uno specifico ruolo per il suo nome

    Optional<User_Role> findByRoleName(RoleNameENUM roleName);

    // verifica l'esistenza di uno specifico ruolo

    boolean existsByRoleName(RoleNameENUM roleName);
}
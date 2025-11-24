//package davidebraghi.CapstoneProject_TimelineManager.repositories;
//
//import davidebraghi.CapstoneProject_TimelineManager.entities.Project_User_Role;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface Project_User_RoleRepository extends JpaRepository<Project_User_Role, Long> {
//
//    // cerca tutti i membri di uno specifico progetto
//
//    List<Project_User_Role> findByProject_ProjectId(Long projectId);
//
//    // cerca tutti i progetti di uno specifico user ha un ruolo (con i ruoli per ciascun progetto)
//
//    List<Project_User_Role> findByUser_UserId(Long userId);
//
//    // cerca tutti gli asseniamenti di uno specifico ruolo
//
//    List<Project_User_Role> findByRole_RoleId(Long roleId);
//
//    // cerca tutti il ruolo di uno specifico user di uno specifico progetto
//
//    Optional<Project_User_Role> findByProject_ProjectIdAndUser_UserId(Long projectId,
//                                                                      Long userId);
//
//    // conta quanti memenri ha uno specifico progetto
//
//    Long countByProject_ProjectId(Long projectId);
//
//    // conta in quanti progetti uno specifico user ha un ruolo
//
//    Long countByUser_UserId(Long userId);
//}
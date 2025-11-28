package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.projectId = :projectId")
    List<ProjectMember> findByProjectProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.projectId = :projectId AND pm.user.userId = :userId")
    ProjectMember findByProjectProjectIdAndUserUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

//  fix da riguardare (metodo moooolto preso in prestito)

    @Modifying
    @Query(value = "DELETE FROM project_members WHERE project_id = :projectId AND user_id = :userId", nativeQuery = true)
    int deleteByProjectIdAndUserIdNative(@Param("projectId") Long projectId, @Param("userId") Long userId);
}



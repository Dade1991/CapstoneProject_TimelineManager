package davidebraghi.CapstoneProject_TimelineManager.runners;

import davidebraghi.CapstoneProject_TimelineManager.entities.Task_Status;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.entities.User_Role;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import davidebraghi.CapstoneProject_TimelineManager.enums.TaskStatusENUM;
import davidebraghi.CapstoneProject_TimelineManager.repositories.Task_StatusRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.UserRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.User_RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private Task_StatusRepository task_statusRepository;
    @Autowired
    private User_RoleRepository user_roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bcrypt;

    // primo entry point eseguito al primo avvio dell'app

    @Override
    public void run(String... args) throws Exception {

        // inizializzo gli status per non creare errori all'avvio

        initializeTaskStatuses();

        // inizializzo gli User_Role per non creare errori all'avvio

        initializeUserRoles();

        // inizializzo gli user per TESTING (eventuale implentazione di Test dedicati nell'apposita sezione)

        initializeUsers();

    }

    private void initializeTaskStatuses() {
        if (task_statusRepository.count() == 0) {
            task_statusRepository.save(new Task_Status(TaskStatusENUM.TO_DO, 1));
            task_statusRepository.save(new Task_Status(TaskStatusENUM.IN_PROGRESS, 2));
            task_statusRepository.save(new Task_Status(TaskStatusENUM.IN_REVIEW, 3));
            task_statusRepository.save(new Task_Status(TaskStatusENUM.UNDER_TESTING, 4));
            task_statusRepository.save(new Task_Status(TaskStatusENUM.PAUSED, 5));
            task_statusRepository.save(new Task_Status(TaskStatusENUM.WAITING_FEEDBACK, 6));
            task_statusRepository.save(new Task_Status(TaskStatusENUM.BLOCKED, 7));
            task_statusRepository.save(new Task_Status(TaskStatusENUM.COMPLETED, 8));
            task_statusRepository.save(new Task_Status(TaskStatusENUM.CANCELLED, 9));
        } else {
            task_statusRepository.count();
        }
    }

    private void initializeUserRoles() {
        if (user_roleRepository.count() == 0) {
            for (RoleNameENUM roleName : RoleNameENUM.values()) {
                if (user_roleRepository.findByRoleName(roleName).isEmpty()) {
                    user_roleRepository.save(new User_Role(roleName));
                }
            }
        }
    }

    private void initializeUsers() {

        // ----------------- ADMIN USER 1-----------------

        createUserIfNotExists(
                "davideB@demo.com",
                "Dade_1991",
                "Davide",
                "Braghi",
                "admin1234"
        );

        // ----------------- TEST USER 2 -----------------

        createUserIfNotExists(
                "claraS@demo.com",
                "Mini",
                "Clara",
                "Schillaci",
                "user1234"
        );

        // ----------------- TEST USER 3 -----------------

        createUserIfNotExists(
                "riccardoM@demo.com",
                "Raik-666",
                "Riccardo",
                "Marra",
                "user1234"
        );

        // ----------------- TEST USER 4 -----------------

        createUserIfNotExists(
                "tizianaB@demo.com",
                "Tiz",
                "Tiziana",
                "Biciocchi",
                "user1234"
        );

        // ----------------- TEST USER 5 -----------------

        createUserIfNotExists(
                "eliaG@demo.com",
                "Eli_20_DND",
                "Elia",
                "Guerci",
                "user1234"
        );
    }

    private void createUserIfNotExists(String email,
                                       String nickname,
                                       String name,
                                       String surname,
                                       String password
    ) {
        if (userRepository.findByEmail(email).isEmpty()) {

            User user = new User();

            user.setEmail(email);
            user.setNickname(nickname);
            user.setName(name);
            user.setSurname(surname);
            user.setPassword(bcrypt.encode(password));
            user.setAvatarUrl("https://ui-avatars.com/api/?name=" + name + "+" + surname);

            userRepository.save(user);
        }
    }
}
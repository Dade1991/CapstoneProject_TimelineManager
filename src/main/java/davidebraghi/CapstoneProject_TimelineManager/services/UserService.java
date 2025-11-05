package davidebraghi.CapstoneProject_TimelineManager.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserRegisterRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.BadRequestException;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.UserRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.User_RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    // variabili per controllo inserimento imageUrl

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_FORMAT = List.of("image/jpeg", "image/png");

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private User_RoleRepository user_roleRepository;
    @Autowired
    private PasswordEncoder bcrypt;
    @Autowired
    private Cloudinary getAvatarImage;

    // FIND_ALL (paginato)

    public Page<User> findAllUsers(int pageNumber,
                                   int pageSize,
                                   String sortBy) {
        if (pageSize > 50) pageSize = 50;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        return this.userRepository.findAll(pageable);
    }

    // FIND_ALL (non-paginato)

    public List<User> findAllUsersWithNoPagination() {
        return this.userRepository.findAll();
    }

    // SAVE

    public User savedUser(UserRegisterRequest payload) {
        this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
            throw new BadRequestException("The email " + user.getEmail() + " is already in use.");
        });

        User newUser = new User(payload.name(),
                payload.surname(),
                payload.nickname(),
                payload.email(),
                bcrypt.encode(payload.password())
        );

        // upload della imageUrl

        newUser.setProfilePicUrl("https://ui-avatars.com/api/?name=" + payload.name() + "+" + payload.surname());

        User savedUser = userRepository.save(newUser);
        log.info("User with ID " + savedUser.getUserId() + " successfully registered.");

        return savedUser;
    }

    // FIND_BY_ID

    public User findUserById(Long userId) {
        return this.userRepository.findById(userId).
                orElseThrow(() -> new NotFoundException("User with ID " + userId + " has not been found."));
    }

    // FIND_BY_EMAIL

    public User findUserByEmail(String email) {
        return this.userRepository.findByEmail(email).
                orElseThrow(() -> new NotFoundException("User with email " + email + " has not been found."));
    }

    // FIND_BY_NICKNAME

    public User findUserByNickname(String nickname) {
        return this.userRepository.findByNickname(nickname).
                orElseThrow(() -> new NotFoundException("User with nickname " + nickname + " has not been found."));
    }

    // FIND_BY_ID_AND_UPDATE

    public User findUserByIdAndUpdate(Long userId, UserRegisterRequest payload) {
        User foundUser = this.findUserById(userId);

        if (!foundUser.getEmail().equals(payload.email())) {
            this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
                throw new BadRequestException("The email " + user.getEmail() + " has not been found.");
            });
        }

        foundUser.setName(payload.name());
        foundUser.setSurname(payload.surname());
        foundUser.setNickname(payload.nickname());
        foundUser.setEmail(payload.email());
        foundUser.setPassword(payload.password());

        User modifiedUser = this.userRepository.save(foundUser);

        log.info("User with ID " + foundUser.getUserId() + " successfully updated.");

        return modifiedUser;
    }

    // FIND_BY_ID_AND_DELETE

    public void findUserByIdAndDelete(Long userId) {
        User foundUser = this.findUserById(userId);
        this.userRepository.delete(foundUser);
    }

    // UPDATE_AVATAR_PROFILE_PIC

    public User uploadAvatarProfilePic(MultipartFile file, Long userId) {
        if (file.isEmpty()) throw new BadRequestException("Empty file.");
        if (file.getSize() > MAX_SIZE)
            throw new BadRequestException("Warning, the file exceeding the limit of 5MB dimension.");
        if (!(ALLOWED_FORMAT.contains(file.getContentType())))
            throw new BadRequestException("Warning, the file is not a supported format (Shall be .jpeg or .png).");

        User foundUser = this.findUserById(userId);

        try {

            // cattura dell'URL dell'immagine

            Map resultMap = getAvatarImage.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) resultMap.get("url");

            // salvataggio dell'imageUrL

            foundUser.setAvatarURL(imageUrl);
            this.userRepository.save(foundUser);
            return foundUser;
        } catch (Exception ex) {
            throw new BadRequestException("Error during image uploading.");
        }
    }
}
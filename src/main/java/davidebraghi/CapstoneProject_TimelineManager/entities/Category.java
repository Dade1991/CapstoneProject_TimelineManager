package davidebraghi.CapstoneProject_TimelineManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "categoryId")
    private Long categoryId;
    @Column(name = "categoryName", nullable = false, unique = true)
    private String categoryName;

    // relazioni

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private Set<Task> tasks = new HashSet<>();

}
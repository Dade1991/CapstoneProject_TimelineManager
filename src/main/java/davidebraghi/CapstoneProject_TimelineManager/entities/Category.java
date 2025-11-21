package davidebraghi.CapstoneProject_TimelineManager.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @Column(name = "categoryName")
    private String categoryName;
    @Column(name = "categoryColor")
    private String categoryColor;

    // relazioni

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    @JsonIgnore
    private Project project;
    @ManyToMany(mappedBy = "categories")
    @JsonBackReference
    private Set<Task> tasks = new HashSet<>();
}
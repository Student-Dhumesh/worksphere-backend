package com.worksphere.backend.project;

import com.worksphere.backend.task.Task;
import com.worksphere.backend.workspace.Workspace;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @OneToMany(mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
}

package apt.project.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "ca_lists")
@Table(
    name = "ca_lists"
)
@Setter
@Getter
@NoArgsConstructor
public class ControlAccessList {
    @EmbeddedId
    private ControlAccessListKey Id;

    @Column(
        nullable = false,
        name = "doc_name"
    )
    private String docName;

    @Column(
        nullable = false,
        name = "permissions"
    )
    private String permissions;
}

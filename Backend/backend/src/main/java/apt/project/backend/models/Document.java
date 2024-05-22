package apt.project.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "documents")
@Table(
    name = "documents",
    uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id","name"})
)
@Setter
@Getter
@NoArgsConstructor
public class Document {
    @SequenceGenerator(
        name = "doc_id_seq",
        sequenceName = "doc_id_seq",
        allocationSize = 1
    )
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "doc_id_seq"
    )
    @Column(
        nullable = false,
        name = "id"
    )
    private long id;

    @Column(
        nullable = false,
        name = "name"
    )
    private String name;

    @Column(
        nullable = false,
        name = "owner_id"
    )
    private long owner_id;
    
    @Column(
        nullable = false,
        name = "content"
    )
    private String content;

    public Document(Long owner_id, String name, String content){
        this.owner_id = owner_id;
        this.content = content;
        this.name = name;
    }
}

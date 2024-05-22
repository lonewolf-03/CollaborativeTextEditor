package apt.project.backend.services;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import apt.project.backend.models.Document;
import jakarta.transaction.Transactional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findById(Long id);

    boolean existsById(Long id);

    @Query(value = "SELECT * FROM documents WHERE owner_id=:user_id", nativeQuery=true)
    Optional<List<Document>> getAllDocumentsOfUID(@Param("user_id") Long user_id);

    @Query(value = "SELECT d.id, d.owner_id, d.content, d.name FROM documents d , users u WHERE d.owner_id=u.id AND u.email= :email",
           nativeQuery = true)
    Optional<List<Document>> getAllDocumentsOfEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE documents SET content= :new_content WHERE id= :did", nativeQuery = true)
    Integer updateDocumentContent(@Param("did") Long did, @Param("new_content") String new_content);

    @Modifying
    @Transactional
    @Query(value = "UPDATE documents SET name= :new_name WHERE id= :did", nativeQuery = true)
    Integer updateDocumentName(@Param("did") Long did, @Param("new_name") String new_name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM documents WHERE id= :did", nativeQuery=true)
    void deleteByIdAndCascade(@Param("did") Long did);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM documents WHERE owner_id= :user_id", nativeQuery=true)
    Integer deleteDocumentsWithOwnerID(@Param("user_id") Long user_id);


    @Transactional
    @Query(value = "SELECT owner_id FROM documents WHERE id= :did", nativeQuery = true)
    Optional<Long> getOwnerId(@Param("did") Long did);
}

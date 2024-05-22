package apt.project.backend.services;

import apt.project.backend.models.User;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email); // spring data JPA automatically creates the implementation of this method based on its name

    Boolean existsByEmail(String email);

    @Query(value = "SELECT id FROM users WHERE email=:user_email", nativeQuery = true)
    Optional<Long> getIdFromEmail(@Param("user_email") String user_email);

    @Query(value = "SELECT role FROM roles WHERE user_id=:uid", nativeQuery = true)
    Optional<String> getUserRole(@Param("uid") Long uid);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO roles (user_id, role) VALUES (:uid, :user_role)", nativeQuery = true)
    void insertUserRole(@Param("uid") Long uid, @Param("user_role") String user_role);

}

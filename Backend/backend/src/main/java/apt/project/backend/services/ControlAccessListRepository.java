package apt.project.backend.services;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import apt.project.backend.models.ControlAccessList;
import apt.project.backend.models.ControlAccessListKey;
import jakarta.transaction.Transactional;

public interface ControlAccessListRepository extends JpaRepository<ControlAccessList, ControlAccessListKey> {
    
    @Query(value = "SELECT * FROM ca_lists WHERE user_id=:uid", nativeQuery = true)
    List<String> getSharedPermissionsFromUID(@Param("uid") Long uid);

    @Query(value = "SELECT * FROM ca_lists WHERE doc_id=:did", nativeQuery = true)
    List<String> getPermissionsOfDocument(@Param("did") Long did);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ca_lists WHERE doc_id=:did AND user_id=:uid", nativeQuery = true)
    void deleteByDocIdAndUserId(@Param("did") Long did, @Param("uid") Long uid);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ca_lists SET permissions=:new_permission WHERE user_id=:uid AND doc_id=:did", nativeQuery = true)
    void updateUserPermission(@Param("did") Long did, @Param("uid") Long uid, @Param("new_permission") String new_permission);
}

package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.Group;
import dubbelf.fr.agendabackend.bo.GroupMember;
import dubbelf.fr.agendabackend.bo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    List<GroupMember> findByGroup(Group group);

    @Query("SELECT gm.user FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<User> findUsersByGroupId(@Param("groupId") UUID groupId);

    Optional<GroupMember> findByGroupIdAndUserId(UUID currentUserId, UUID groupId);
}

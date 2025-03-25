package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.Group;
import dubbelf.fr.agendabackend.bo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    List<Group> findByCreatedById(UUID userId);

    List<Group> findByUsersContains(Optional<User> user);

    Group findGroupById(UUID groupId);
}

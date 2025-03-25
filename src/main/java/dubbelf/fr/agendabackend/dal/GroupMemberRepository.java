package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.Group;
import dubbelf.fr.agendabackend.bo.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    List<GroupMember> findByGroup(Group group);
}

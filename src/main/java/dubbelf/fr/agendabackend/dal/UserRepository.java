package dubbelf.fr.agendabackend.dal;
import dubbelf.fr.agendabackend.bo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>{
    User findByEmail(String email);

    User findBypseudo(String pseudo);

    boolean existsByEmail(String email);

    boolean existsByPseudo(String username);
}

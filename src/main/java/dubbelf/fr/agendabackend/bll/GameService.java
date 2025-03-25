package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.Game;
import dubbelf.fr.agendabackend.dal.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public Game createGame(Game game) {
        return gameRepository.save(game);
    }

    public Game updateGame(UUID gameId, Game gameDetails) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);

        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            game.setName(gameDetails.getName());
            game.setDescription(gameDetails.getDescription());
            game.setSettings(gameDetails.getSettings());
            return gameRepository.save(game);
        } else {
            throw new RuntimeException("Game not found");
        }
    }

    public void deleteGame(UUID gameId) {
        if (gameRepository.existsById(gameId)) {
            gameRepository.deleteById(gameId);
        } else {
            throw new RuntimeException("Game not found");
        }
    }

    public Game getGameById(UUID gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
}

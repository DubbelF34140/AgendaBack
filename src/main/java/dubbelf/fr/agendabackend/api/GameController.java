package dubbelf.fr.agendabackend.api;

import dubbelf.fr.agendabackend.bll.GameService;
import dubbelf.fr.agendabackend.bo.Game;
import dubbelf.fr.agendabackend.dto.GameCreateDTO;
import dubbelf.fr.agendabackend.dto.RespondGameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody GameCreateDTO gameCreateDTO) {
        Game createdGame = gameService.createGame(gameCreateDTO);
        return ResponseEntity.ok(createdGame);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGameById(@PathVariable UUID gameId) {
        return ResponseEntity.ok(gameService.getGameById(gameId));
    }

    @GetMapping
    public ResponseEntity<List<RespondGameDTO>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @PutMapping("/{gameId}")
    public ResponseEntity<Game> updateGame(@PathVariable UUID gameId, @RequestBody Game game) {
        return ResponseEntity.ok(gameService.updateGame(gameId, game));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<String> deleteGame(@PathVariable UUID gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.ok("Game deleted successfully.");
    }
}

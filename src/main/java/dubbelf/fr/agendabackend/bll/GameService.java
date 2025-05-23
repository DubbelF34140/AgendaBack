package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.Game;
import dubbelf.fr.agendabackend.bo.GameSetting;
import dubbelf.fr.agendabackend.bo.PlayerGameSetting;
import dubbelf.fr.agendabackend.dal.GameRepository;
import dubbelf.fr.agendabackend.dal.GameSettingRepository;
import dubbelf.fr.agendabackend.dal.PlayerGameSettingRepository;
import dubbelf.fr.agendabackend.dto.GameCreateDTO;
import dubbelf.fr.agendabackend.dto.RespondGameDTO;
import dubbelf.fr.agendabackend.dto.RespondGamePlayerSettingDTO;
import dubbelf.fr.agendabackend.dto.RespondGameSettingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameSettingRepository gameSettingRepository;
    @Autowired
    private PlayerGameSettingRepository playerGameSettingRepository;

    public Game createGame(GameCreateDTO gameCreateDTO) {
        Game game = new Game();
        game.setName(gameCreateDTO.getName());
        game.setDescription(gameCreateDTO.getDescription());
        game.setAvatarUrl(gameCreateDTO.getAvatarUrl());

        // Sauvegarde du jeu en base avant d'ajouter les settings
        Game savedGame = gameRepository.save(game);

        List<GameSetting> settings = gameCreateDTO.getSettings().stream().map(dto -> {
            GameSetting setting = new GameSetting();
            setting.setGame(savedGame);
            setting.setKey(dto.getKey());
            setting.setValueType(dto.getValueType());
            setting.setDefaultValue(dto.getDefaultValue());
            return setting;
        }).collect(Collectors.toList());

        gameSettingRepository.saveAll(settings);
        game.setSettings(settings);

        List<PlayerGameSetting> playersettings = gameCreateDTO.getPlayersettings().stream().map(dto -> {
            PlayerGameSetting setting = new PlayerGameSetting();
            setting.setGame(savedGame);
            setting.setKey(dto.getKey());
            setting.setValueType(dto.getValueType());
            setting.setDefaultValue(dto.getDefaultValue());
            return setting;
        }).collect(Collectors.toList());

        playerGameSettingRepository.saveAll(playersettings);
        game.setPlayersettings(playersettings);
        return game;
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

    public RespondGameDTO getGameById(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));
        return new RespondGameDTO(
                game.getId(),
                game.getName(),
                game.getDescription(),
                game.getAvatarUrl(),
                game.getSettings().stream().map(setting -> new RespondGameSettingDTO(
                        setting.getId(),
                        setting.getKey(),
                        setting.getValueType(),
                        setting.getDefaultValue()
                )).collect(Collectors.toList()),
                game.getPlayersettings().stream().map(setting -> new RespondGamePlayerSettingDTO(
                        setting.getId(),
                        setting.getKey(),
                        setting.getValueType(),
                        setting.getDefaultValue()
                )).collect(Collectors.toList()));
    }

    public List<RespondGameDTO> getAllGames() {
        List<Game> games = gameRepository.findAll();

        return games.stream().map(game -> new RespondGameDTO(
                game.getId(),
                game.getName(),
                game.getDescription(),
                game.getAvatarUrl(),
                game.getSettings().stream().map(setting -> new RespondGameSettingDTO(
                        setting.getId(),
                        setting.getKey(),
                        setting.getValueType(),
                        setting.getDefaultValue()
                )).collect(Collectors.toList()),
                game.getPlayersettings().stream().map(setting -> new RespondGamePlayerSettingDTO(
                        setting.getId(),
                        setting.getKey(),
                        setting.getValueType(),
                        setting.getDefaultValue()
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

}

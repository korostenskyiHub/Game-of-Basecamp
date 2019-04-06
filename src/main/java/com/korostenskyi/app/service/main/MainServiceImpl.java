package com.korostenskyi.app.service.main;

import com.korostenskyi.app.data.entity.Book;
import com.korostenskyi.app.data.entity.Character;
import com.korostenskyi.app.data.entity.House;
import com.korostenskyi.app.data.repository.BookRepository;
import com.korostenskyi.app.data.repository.CharacterRepository;
import com.korostenskyi.app.data.repository.HouseRepository;
import com.korostenskyi.app.exception.NoConnectionException;
import com.korostenskyi.app.exception.NoSuchElementException;
import com.korostenskyi.app.service.concurrent.ConcurrentTaskService;
import com.korostenskyi.app.service.generator.NumberGenerator;
import com.korostenskyi.app.wire.response.AllCharactersResponse;
import com.korostenskyi.app.wire.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class MainServiceImpl implements MainService {

    private final ConcurrentTaskService taskService;
    private NumberGenerator numberGenerator;

    private BookRepository bookRepository;
    private CharacterRepository characterRepository;
    private HouseRepository houseRepository;

    private Logger logger;

    @Autowired
    public MainServiceImpl(ConcurrentTaskService taskService) {
        this.taskService = taskService;

        logger = LoggerFactory.getLogger(MainServiceImpl.class);
    }

    @Autowired
    public void setNumberGenerator(NumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    public void setCharacterRepository(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Autowired
    public void setHouseRepository(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    @Override
    public AllCharactersResponse fetchAllCharactersFromDatabase(Integer page, Integer pageSize) {
        try {
            return new AllCharactersResponse(HttpStatus.OK, taskService.fetchAllCharactersFromDatabase(page, pageSize).get());
        } catch (InterruptedException | ExecutionException e) {
            throw new NoConnectionException("Some connection troubles!");
        }
    }

    @Override
    public MessageResponse fight(Long characterId1, Long characterId2) {

        byte generatedId = numberGenerator.generateWinner();

        try {
            Character character1 = taskService.fetchCharacterByIdFromDatabase(characterId1).get();
            Character character2 = taskService.fetchCharacterByIdFromDatabase(characterId2).get();

            if (generatedId == 1) {
                return new MessageResponse(HttpStatus.OK, character1.getName());
            } else {
                return new MessageResponse(HttpStatus.OK, character2.getName());
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new NoSuchElementException("There is no such character in database!");
        }
    }

    @Override
    public MessageResponse postRandomCharacter() {
        numberGenerator.generateNumber();

        Long generatedLong = numberGenerator.getGeneratedNumber();

        logger.info(String.valueOf(generatedLong));

        Character character = getCharacterById(generatedLong);

        logger.info(character.getName() + " " + character.getId());

        return new MessageResponse(HttpStatus.OK, String.valueOf(character.getId()));
    }

    @Override
    public Book getBookById(Long id) {

        Book book = null;

        try {
            book = taskService.getBookById(id).get();
            bookRepository.save(book);

            logger.info("Book saved");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return book;
    }

    @Override
    public Character getCharacterById(Long id) {

        Character character = null;

        try {
            character = taskService.getCharacterById(id).get();
            characterRepository.save(character);

            logger.info("Character saved");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return character;
    }

    @Override
    public House getHouseById(Long id) {

        House house = null;

        try {
            house = taskService.getHouseById(id).get();
            houseRepository.save(house);

            logger.info("House saved");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return house;
    }

    @Override
    public void stopExecutor() {

        logger.warn("Telling the task executor to stop...");

        taskService.stopService();

        logger.info("Task executor is stopped");
    }
}

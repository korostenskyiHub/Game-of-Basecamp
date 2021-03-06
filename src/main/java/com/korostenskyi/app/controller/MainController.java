package com.korostenskyi.app.controller;

import com.korostenskyi.app.data.entity.Book;
import com.korostenskyi.app.data.entity.Character;
import com.korostenskyi.app.data.entity.House;
import com.korostenskyi.app.service.main.MainService;
import com.korostenskyi.app.wire.response.AllCharactersResponse;
import com.korostenskyi.app.wire.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class MainController {

    private final MainService mainService;

    private Logger logger;

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;

        logger = LoggerFactory.getLogger(MainController.class);
    }

    @PostMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> postRandomCharacter() {

        logger.info("Posting random character to the database");

        return ResponseEntity.ok().body(mainService.postRandomCharacter());
    }

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AllCharactersResponse> fetchAllCharactersFromDatabase(@RequestParam Integer page, @RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize) {

        logger.info("Fetching all characters from database...");

        if (pageSize.isEmpty()) {
            return ResponseEntity.ok().body(mainService.fetchAllCharactersFromDatabase(page, 5));
        } else {
            return ResponseEntity.ok().body(mainService.fetchAllCharactersFromDatabase(page, pageSize.get()));
        }
    }

    @GetMapping(value = "/{UUID}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> fight(@PathVariable Long UUID, @RequestParam(value = "enemy") Long UUID2) {

        logger.info("Fetching character from local database");

        return ResponseEntity.ok().body(mainService.fight(UUID, UUID2));
    }

    @GetMapping(value = "/book/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {

        logger.info("Fetching book with id of " + id);

        return ResponseEntity.ok().body(mainService.getBookById(id));
    }

    @GetMapping(value = "/character/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Character> getCharacterById(@PathVariable Long id) {

        logger.info("Fetching character with id of " + id);

        return ResponseEntity.ok().body(mainService.getCharacterById(id));
    }

    @GetMapping(value = "/houses/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<House> getHouseById(@PathVariable Long id) {

        logger.info("Fetching house with id of " + id);

        return ResponseEntity.ok().body(mainService.getHouseById(id));
    }
}

package org.suffleport.zwloader.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.suffleport.zwloader.domain.Card;
import org.suffleport.zwloader.service.CardService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/{uid}")
    public Card get(@PathVariable String uid) { return cardService.getByUidOrThrow(uid); }

    @PostMapping
    public Card create(@RequestBody CreateCardRequest req) {
        return cardService.createCard(req.getUid(), req.getPersonId());
    }

    @PostMapping("/{uid}/activate")
    public Card activate(@PathVariable String uid) { return cardService.activate(uid); }

    @PostMapping("/{uid}/deactivate")
    public Card deactivate(@PathVariable String uid) { return cardService.deactivate(uid); }

    @PutMapping("/{uid}/reassign/{personId}")
    public Card reassign(@PathVariable String uid, @PathVariable UUID personId) {
        return cardService.reassignOwner(uid, personId);
    }

    @DeleteMapping("/{uid}")
    public void delete(@PathVariable String uid) { cardService.deleteByUid(uid); }

    @DeleteMapping("/by-person/{personId}")
    public void deleteByPerson(@PathVariable UUID personId) { cardService.deleteAllByPerson(personId); }

    @GetMapping("/by-person/{personId}")
    public List<Card> byPerson(@PathVariable UUID personId) { return cardService.findByPerson(personId); }

    @GetMapping("/by-person/{personId}/active")
    public List<Card> activeByPerson(@PathVariable UUID personId) { return cardService.findActiveByPerson(personId); }

    @GetMapping("/by-person/{personId}/inactive")
    public List<Card> inactiveByPerson(@PathVariable UUID personId) { return cardService.findInactiveByPerson(personId); }

    @GetMapping("/search")
    public List<Card> search(@RequestParam("q") String fragment) { return cardService.searchByUidFragment(fragment); }

    @GetMapping("/created-after")
    public List<Card> createdAfter(@RequestParam("date") OffsetDateTime date) { return cardService.listCreatedAfter(date); }

    @GetMapping("/by-person/{personId}/count-active")
    public long countActive(@PathVariable UUID personId) { return cardService.countActiveByPerson(personId); }

    @Data
    public static class CreateCardRequest {
        private String uid;
        private UUID personId;
    }
}


package org.suffleport.zwloader.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.suffleport.zwloader.domain.Guest;
import org.suffleport.zwloader.service.GuestService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @GetMapping
    public List<Guest> listAll() { return guestService.listAll(); }

    @GetMapping("/{id}")
    public Guest get(@PathVariable UUID id) { return guestService.getOrThrow(id); }

    @PostMapping
    public Guest create(@RequestBody CreateGuestRequest req) {
        return guestService.create(req.getLastName(), req.getFirstName(), req.getMiddleName(), req.getPhone(), req.getCompany());
    }

    @PutMapping("/{id}")
    public Guest update(@PathVariable UUID id, @RequestBody UpdateGuestRequest req) {
        return guestService.update(id, req.getLastName(), req.getFirstName(), req.getMiddleName(), req.getPhone(), req.getCompany(), req.getDocument(), req.getNotes());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { guestService.delete(id); }

    @GetMapping("/search/by-last-name")
    public List<Guest> byLastName(@RequestParam("lastName") String lastName) { return guestService.findByLastName(lastName); }

    @GetMapping("/search/by-document")
    public List<Guest> byDocument(@RequestParam("q") String fragment) { return guestService.searchByDocument(fragment); }

    @Data
    public static class CreateGuestRequest {
        private String lastName;
        private String firstName;
        private String middleName;
        private String phone;
        private String company;
    }

    @Data
    public static class UpdateGuestRequest {
        private String lastName;
        private String firstName;
        private String middleName;
        private String phone;
        private String company;
        private String document;
        private String notes;
    }
}


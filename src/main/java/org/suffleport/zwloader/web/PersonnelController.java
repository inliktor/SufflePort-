package org.suffleport.zwloader.web;

import org.suffleport.zwloader.domain.Personnel;
import org.suffleport.zwloader.service.PersonnelService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    @GetMapping
    public List<Personnel> getAll() {
        return personnelService.listAll();
    }

    @GetMapping("/{id}")
    public Personnel getById(@PathVariable UUID id) {
        return personnelService.getOrThrow(id);
    }

    @PostMapping
    public Personnel create(@RequestBody Personnel body) {
        LocalDate dob = body.getDateOfBirth();
        UUID positionId = body.getPosition() != null ? body.getPosition().getId() : null;
        return personnelService.create(
                body.getLastName(),
                body.getFirstName(),
                body.getMiddleName(),
                dob,
                positionId,
                body.getPhone()
        );
    }

    @PutMapping("/{id}")
    public Personnel update(@PathVariable UUID id, @RequestBody Personnel body) {
        UUID positionId = body.getPosition() != null ? body.getPosition().getId() : null;
        return personnelService.update(
                id,
                body.getLastName(),
                body.getFirstName(),
                body.getMiddleName(),
                body.getDateOfBirth(),
                positionId,
                body.getPhone()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        personnelService.delete(id);
    }
}

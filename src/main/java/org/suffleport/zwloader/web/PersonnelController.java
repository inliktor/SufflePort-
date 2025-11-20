package org.suffleport.zwloader.web;

import lombok.Data;
import org.suffleport.zwloader.domain.Personnel;
import org.suffleport.zwloader.service.PersonnelService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public Personnel create(@RequestBody CreatePersonnelRequest req) {
        UUID positionId = req.getPositionId();
        return personnelService.create(
                req.getLastName(),
                req.getFirstName(),
                req.getMiddleName(),
                req.getDateOfBirth(),
                positionId,
                req.getPhone()
        );
    }
    
    @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    public Personnel uploadAvatar(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        return personnelService.uploadAvatar(id, file);
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
    
    @Data
    public static class CreatePersonnelRequest {
        private String lastName;
        private String firstName;
        private String middleName;
        private LocalDate dateOfBirth;
        private UUID positionId;
        private String phone;
    }
}

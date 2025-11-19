package org.suffleport.zwloader.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.suffleport.zwloader.domain.Position;
import org.suffleport.zwloader.service.PositionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public List<Position> listAll() { return positionService.listAll(); }

    @GetMapping("/{id}")
    public Position get(@PathVariable UUID id) { return positionService.getOrThrow(id); }

    @GetMapping("/by-name")
    public Position findByName(@RequestParam("name") String name) { return positionService.findByName(name); }

    @GetMapping("/search/prefix")
    public List<Position> findByPrefix(@RequestParam("q") String prefix) { return positionService.findByNamePrefix(prefix); }

    @PostMapping
    public Position create(@RequestBody CreatePositionRequest req) { return positionService.create(req.getName(), req.getAccessLevel()); }

    @PutMapping("/{id}")
    public Position update(@PathVariable UUID id, @RequestBody UpdatePositionRequest req) { return positionService.update(id, req.getName(), req.getAccessLevel()); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { positionService.delete(id); }

    @Data
    public static class CreatePositionRequest { private String name; private Integer accessLevel; }

    @Data
    public static class UpdatePositionRequest { private String name; private Integer accessLevel; }
}


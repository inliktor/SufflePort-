package org.suffleport.zwloader.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.suffleport.zwloader.domain.*;
import org.suffleport.zwloader.service.EventService;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<Event> listAll() { return eventService.listAll(); }

    @GetMapping("/{id}")
    public Event get(@PathVariable Long id) { return eventService.getOrThrow(id); }

    @GetMapping("/by-person/{personId}")
    public List<Event> byPerson(@PathVariable java.util.UUID personId) {
        return eventService.listByPerson(personId);
    }

    @GetMapping("/by-card/{uid}")
    public List<Event> byCard(@PathVariable String uid) { return eventService.listByCard(uid); }

    @GetMapping("/by-device/{deviceId}")
    public List<Event> byDevicePeriod(@PathVariable String deviceId,
                                      @RequestParam("start") OffsetDateTime start,
                                      @RequestParam("end") OffsetDateTime end) {
        return eventService.listByDeviceAndPeriod(deviceId, start, end);
    }

    @GetMapping("/by-source")
    public List<Event> bySource(@RequestParam("source") Source source) { return eventService.findBySource(source); }

    @GetMapping("/created-after")
    public List<Event> createdAfter(@RequestParam("date") OffsetDateTime date) { return eventService.listCreatedAfter(date); }

    @GetMapping("/between")
    public List<Event> between(@RequestParam("start") OffsetDateTime start,
                               @RequestParam("end") OffsetDateTime end) {
        return eventService.listBetween(start, end);
    }

    @PostMapping
    public Event create(@RequestBody CreateEventRequest req) {
        return eventService.create(
                req.getCardUid(),
                req.getPersonId(),
                req.getDeviceId(),
                req.getFaceName(),
                req.getDirection(),
                req.getSource(),
                req.getMeta()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { eventService.delete(id); }

    @Data
    public static class CreateEventRequest {
        private String cardUid;
        private java.util.UUID personId;
        private String deviceId;
        private String faceName;
        private Direction direction;
        private Source source;
        private EventMeta meta;
    }
}


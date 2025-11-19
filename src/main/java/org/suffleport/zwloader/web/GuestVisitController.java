package org.suffleport.zwloader.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.suffleport.zwloader.domain.GuestVisit;
import org.suffleport.zwloader.service.GuestVisitService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/guest-visits")
@RequiredArgsConstructor
public class GuestVisitController {

    private final GuestVisitService guestVisitService;

    @GetMapping
    public List<GuestVisit> listAll() { return guestVisitService.listAll(); }

    @GetMapping("/{id}")
    public GuestVisit get(@PathVariable Long id) { return guestVisitService.getOrThrow(id); }

    @GetMapping("/by-guest/{guestId}")
    public List<GuestVisit> byGuest(@PathVariable UUID guestId) { return guestVisitService.listByGuest(guestId); }

    @GetMapping("/by-host/{hostId}")
    public List<GuestVisit> byHost(@PathVariable UUID hostId) { return guestVisitService.listByHost(hostId); }

    @GetMapping("/planned-between")
    public List<GuestVisit> plannedBetween(@RequestParam("start") OffsetDateTime start,
                                           @RequestParam("end") OffsetDateTime end) {
        return guestVisitService.listPlannedBetween(start, end);
    }

    @PostMapping
    public GuestVisit create(@RequestBody CreateGuestVisitRequest req) {
        return guestVisitService.create(req.getGuestId(), req.getHostPersonId(), req.getPlannedFrom(), req.getPlannedTo(), req.getReason());
    }

    @PutMapping("/{id}/plan")
    public GuestVisit updatePlan(@PathVariable Long id, @RequestBody UpdatePlanRequest req) {
        return guestVisitService.updatePlan(id, req.getPlannedFrom(), req.getPlannedTo(), req.getReason());
    }

    @PutMapping("/{id}/status")
    public GuestVisit setStatus(@PathVariable Long id, @RequestParam("status") String status) {
        return guestVisitService.setStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { guestVisitService.delete(id); }

    @Data
    public static class CreateGuestVisitRequest {
        private UUID guestId;
        private UUID hostPersonId;
        private OffsetDateTime plannedFrom;
        private OffsetDateTime plannedTo;
        private String reason;
    }

    @Data
    public static class UpdatePlanRequest {
        private OffsetDateTime plannedFrom;
        private OffsetDateTime plannedTo;
        private String reason;
    }
}


package org.suffleport.zwloader.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.suffleport.zwloader.domain.Device;
import org.suffleport.zwloader.service.DeviceService;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public List<Device> list() { return deviceService.listAll(); }

    @GetMapping("/{id}")
    public Device get(@PathVariable String id) { return deviceService.getOrThrow(id); }

    @GetMapping("/by-kind")
    public List<Device> byKind(@RequestParam("kind") String kind) { return deviceService.findByKind(kind); }

    @GetMapping("/search/location")
    public List<Device> searchByLocation(@RequestParam("q") String fragment) { return deviceService.searchByLocation(fragment); }

    @PostMapping
    public Device create(@RequestBody CreateDeviceRequest req) { return deviceService.create(req.getId(), req.getKind(), req.getLocation()); }

    @PutMapping("/{id}")
    public Device update(@PathVariable String id, @RequestBody UpdateDeviceRequest req) { return deviceService.update(id, req.getKind(), req.getLocation()); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { deviceService.delete(id); }

    @Data
    public static class CreateDeviceRequest { private String id; private String kind; private String location; }

    @Data
    public static class UpdateDeviceRequest { private String kind; private String location; }
}


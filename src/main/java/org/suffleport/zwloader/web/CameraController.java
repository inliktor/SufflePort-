package org.suffleport.zwloader.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.suffleport.zwloader.domain.Camera;
import org.suffleport.zwloader.service.CameraService;

import java.util.List;

@RestController
@RequestMapping("/api/cameras")
@RequiredArgsConstructor
public class CameraController {

    private final CameraService cameraService;

    @GetMapping
    public List<Camera> list() { return cameraService.listAll(); }

    @GetMapping("/{id}")
    public Camera get(@PathVariable String id) { return cameraService.getOrThrow(id); }

    @GetMapping("/by-device/{deviceId}")
    public List<Camera> byDevice(@PathVariable String deviceId) { return cameraService.findByDevice(deviceId); }

    @PostMapping
    public Camera create(@RequestBody CreateCameraRequest req) {
        return cameraService.create(req.getId(), req.getName(), req.getRtspUrl(), req.getLocation(), req.getDeviceId());
    }

    @PutMapping("/{id}")
    public Camera updateBasic(@PathVariable String id, @RequestBody UpdateCameraRequest req) {
        return cameraService.updateBasic(id, req.getName(), req.getRtspUrl(), req.getLocation());
    }

    @PutMapping("/{id}/reassign-device")
    public Camera reassignDevice(@PathVariable String id, @RequestParam(name = "deviceId", required = false) String deviceId) {
        return cameraService.reassignDevice(id, deviceId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { cameraService.delete(id); }

    @Data
    public static class CreateCameraRequest {
        private String id;
        private String name;
        private String rtspUrl;
        private String location;
        private String deviceId;
    }

    @Data
    public static class UpdateCameraRequest {
        private String name;
        private String rtspUrl;
        private String location;
    }
}


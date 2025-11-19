package org.suffleport.zwloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suffleport.zwloader.domain.Camera;
import org.suffleport.zwloader.domain.Device;
import org.suffleport.zwloader.repository.CameraRepository;
import org.suffleport.zwloader.repository.DeviceRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CameraService {

    private final CameraRepository cameraRepository;
    private final DeviceRepository deviceRepository;

    @Transactional
    public Camera create(String id, String name, String rtspUrl, String location, String deviceId) {
        validate(id, name, rtspUrl);
        if (cameraRepository.existsById(id)) {
            throw new IllegalStateException("Камера уже существует: " + id);
        }
        Camera cam = new Camera();
        cam.setId(id);
        cam.setName(name);
        cam.setRtspUrl(rtspUrl);
        cam.setLocation(location);
        if (deviceId != null && !deviceId.isBlank()) {
            Device dev = deviceRepository.findById(deviceId)
                    .orElseThrow(() -> new NoSuchElementException("Устройство не найдено: " + deviceId));
            cam.setDevice(dev);
        }
        return cameraRepository.save(cam);
    }

    @Transactional(readOnly = true)
    public Camera getOrThrow(String id) {
        return cameraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Камера не найдена: " + id));
    }

    @Transactional(readOnly = true)
    public List<Camera> listAll() {
        return cameraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Camera> findByDevice(String deviceId) {
        Objects.requireNonNull(deviceId, "deviceId is required");
        return cameraRepository.findByDevice_Id(deviceId);
    }

    @Transactional
    public Camera updateBasic(String id, String name, String rtspUrl, String location) {
        Camera cam = getOrThrow(id);
        if (name != null) cam.setName(name);
        if (rtspUrl != null) cam.setRtspUrl(rtspUrl);
        if (location != null) cam.setLocation(location);
        return cameraRepository.save(cam);
    }

    @Transactional
    public Camera reassignDevice(String id, String deviceId) {
        Camera cam = getOrThrow(id);
        if (deviceId == null || deviceId.isBlank()) {
            cam.setDevice(null);
        } else {
            Device dev = deviceRepository.findById(deviceId)
                    .orElseThrow(() -> new NoSuchElementException("Устройство не найдено: " + deviceId));
            cam.setDevice(dev);
        }
        return cameraRepository.save(cam);
    }

    @Transactional
    public void delete(String id) {
        Camera cam = getOrThrow(id);
        cameraRepository.delete(cam);
    }

    private void validate(String id, String name, String rtspUrl) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (rtspUrl == null || rtspUrl.isBlank()) throw new IllegalArgumentException("rtspUrl is required");
    }
}


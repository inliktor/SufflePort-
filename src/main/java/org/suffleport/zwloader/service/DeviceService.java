package org.suffleport.zwloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suffleport.zwloader.domain.Device;
import org.suffleport.zwloader.repository.DeviceRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional
    public Device create(String id, String kind, String location) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is required");
        if (deviceRepository.existsById(id)) throw new IllegalStateException("Устройство уже существует: " + id);
        Device d = new Device();
        d.setId(id);
        d.setKind(kind);
        d.setLocation(location);
        return deviceRepository.save(d);
    }

    @Transactional(readOnly = true)
    public Device getOrThrow(String id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Устройство не найдено: " + id));
    }

    @Transactional
    public Device update(String id, String kind, String location) {
        Device d = getOrThrow(id);
        if (kind != null) d.setKind(kind);
        if (location != null) d.setLocation(location);
        return deviceRepository.save(d);
    }

    @Transactional
    public void delete(String id) {
        Device d = getOrThrow(id);
        deviceRepository.delete(d);
    }

    @Transactional(readOnly = true)
    public List<Device> findByKind(String kind) {
        return deviceRepository.findByKind(kind);
    }

    @Transactional(readOnly = true)
    public List<Device> searchByLocation(String fragment) {
        if (fragment == null || fragment.isBlank()) return List.of();
        return deviceRepository.findByLocationContainingIgnoreCase(fragment);
    }

    @Transactional(readOnly = true)
    public List<Device> listAll() { return deviceRepository.findAll(); }
}


package com.agriyield.farmservice.infrastructure.adapter.outgoing.storage;

import com.agriyield.farmservice.application.port.outgoing.PhotoStoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

// Stub implementation — MinIO will be wired when added to Docker Compose
@Slf4j
@Component
public class MinioPhotoStorageAdapter implements PhotoStoragePort {

    @Override
    public String uploadPhoto(UUID farmId, String photoType, MultipartFile file) {
        log.info("STUB: Uploading photo for farm: {}, type: {}, filename: {}",
            farmId, photoType, file.getOriginalFilename());
        // Returns a mock URL — replace with real MinIO upload when configured
        return "http://localhost:9000/farm-photos/" + farmId + "/" +
               photoType.toLowerCase() + "/" + UUID.randomUUID() + ".jpg";
    }

    @Override
    public void deletePhoto(String photoUrl) {
        log.info("STUB: Deleting photo: {}", photoUrl);
    }
}

package com.agriyield.farmservice.application.port.outgoing;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface PhotoStoragePort {

    // Upload photo to MinIO and return the object URL
    // SRS Page 20 — photos stored at MinIO object URL
    String uploadPhoto(UUID farmId, String photoType, MultipartFile file);

    void deletePhoto(String photoUrl);
}

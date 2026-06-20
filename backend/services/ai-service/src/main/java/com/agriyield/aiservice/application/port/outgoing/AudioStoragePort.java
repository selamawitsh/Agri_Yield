package com.agriyield.aiservice.application.port.outgoing;

import org.springframework.web.multipart.MultipartFile;

public interface AudioStoragePort {

    String storeAudioInput(MultipartFile audioFile, String farmId);

    String storeAudioResponse(
            byte[] audioBytes,
            String farmId,
            String sessionId
    );
}
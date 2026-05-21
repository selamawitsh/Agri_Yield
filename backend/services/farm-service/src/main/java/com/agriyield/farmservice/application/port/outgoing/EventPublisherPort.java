package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.Farm;
import com.agriyield.farmservice.domain.model.FarmPhoto;
import com.agriyield.farmservice.domain.model.InputNeed;

public interface EventPublisherPort {

    // SRS Section 5.2 — farm.registered
    void publishFarmRegistered(Farm farm);

    // SRS Section 5.2 — input.needs.created
    void publishInputNeedsCreated(Farm farm, InputNeed inputNeed);

    // SRS Section 5.2 — crop.photo.uploaded
    void publishCropPhotoUploaded(FarmPhoto photo, Farm farm);
}

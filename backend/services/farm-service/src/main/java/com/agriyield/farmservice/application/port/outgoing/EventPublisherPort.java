package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.CropCycle;
import com.agriyield.farmservice.domain.model.Farm;
import com.agriyield.farmservice.domain.model.FarmPhoto;
import com.agriyield.farmservice.domain.model.InputNeed;

public interface EventPublisherPort {
    // SRS Section 5.2 — farm.registered
    void publishFarmRegistered(Farm farm);
    // SRS Section 5.2 — input.needs.created
    void publishInputNeedsCreated(Farm farm, InputNeed inputNeed, String seasonName);
    // SRS Section 5.2 — crop.photo.uploaded
    void publishCropPhotoUploaded(FarmPhoto photo, Farm farm);
    // SRS Section 5.2 — farm.planted
    void publishFarmPlanted(Farm farm, CropCycle cropCycle);
}

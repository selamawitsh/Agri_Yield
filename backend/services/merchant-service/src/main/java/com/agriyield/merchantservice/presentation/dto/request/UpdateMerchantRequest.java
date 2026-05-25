package com.agriyield.merchantservice.presentation.dto.request;

import lombok.Data;

@Data
public class UpdateMerchantRequest {
    private String businessName;
    private Double storeGpsLat;
    private Double storeGpsLng;
    private String telebirrAccount;
}

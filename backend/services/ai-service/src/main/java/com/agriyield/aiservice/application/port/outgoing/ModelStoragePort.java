package com.agriyield.aiservice.application.port.outgoing;

public interface ModelStoragePort {

    String downloadModel(String modelName);

    boolean modelExists(String modelName);
}
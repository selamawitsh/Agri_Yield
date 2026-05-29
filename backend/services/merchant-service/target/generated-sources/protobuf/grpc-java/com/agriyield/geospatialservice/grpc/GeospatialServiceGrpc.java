package com.agriyield.geospatialservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: geospatial_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GeospatialServiceGrpc {

  private GeospatialServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.geospatialservice.grpc.GeospatialService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse> getStartMonitoringMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StartMonitoring",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse> getStartMonitoringMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse> getStartMonitoringMethod;
    if ((getStartMonitoringMethod = GeospatialServiceGrpc.getStartMonitoringMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getStartMonitoringMethod = GeospatialServiceGrpc.getStartMonitoringMethod) == null) {
          GeospatialServiceGrpc.getStartMonitoringMethod = getStartMonitoringMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StartMonitoring"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("StartMonitoring"))
              .build();
        }
      }
    }
    return getStartMonitoringMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse> getRegisterFarmPolygonMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterFarmPolygon",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse> getRegisterFarmPolygonMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse> getRegisterFarmPolygonMethod;
    if ((getRegisterFarmPolygonMethod = GeospatialServiceGrpc.getRegisterFarmPolygonMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getRegisterFarmPolygonMethod = GeospatialServiceGrpc.getRegisterFarmPolygonMethod) == null) {
          GeospatialServiceGrpc.getRegisterFarmPolygonMethod = getRegisterFarmPolygonMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterFarmPolygon"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("RegisterFarmPolygon"))
              .build();
        }
      }
    }
    return getRegisterFarmPolygonMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse> getValidatePolygonMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidatePolygon",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse> getValidatePolygonMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse> getValidatePolygonMethod;
    if ((getValidatePolygonMethod = GeospatialServiceGrpc.getValidatePolygonMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getValidatePolygonMethod = GeospatialServiceGrpc.getValidatePolygonMethod) == null) {
          GeospatialServiceGrpc.getValidatePolygonMethod = getValidatePolygonMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidatePolygon"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("ValidatePolygon"))
              .build();
        }
      }
    }
    return getValidatePolygonMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse> getCalculateFarmAreaMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CalculateFarmArea",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse> getCalculateFarmAreaMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse> getCalculateFarmAreaMethod;
    if ((getCalculateFarmAreaMethod = GeospatialServiceGrpc.getCalculateFarmAreaMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getCalculateFarmAreaMethod = GeospatialServiceGrpc.getCalculateFarmAreaMethod) == null) {
          GeospatialServiceGrpc.getCalculateFarmAreaMethod = getCalculateFarmAreaMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CalculateFarmArea"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("CalculateFarmArea"))
              .build();
        }
      }
    }
    return getCalculateFarmAreaMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse> getDetectSpatialOverlapMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DetectSpatialOverlap",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse> getDetectSpatialOverlapMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse> getDetectSpatialOverlapMethod;
    if ((getDetectSpatialOverlapMethod = GeospatialServiceGrpc.getDetectSpatialOverlapMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getDetectSpatialOverlapMethod = GeospatialServiceGrpc.getDetectSpatialOverlapMethod) == null) {
          GeospatialServiceGrpc.getDetectSpatialOverlapMethod = getDetectSpatialOverlapMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DetectSpatialOverlap"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("DetectSpatialOverlap"))
              .build();
        }
      }
    }
    return getDetectSpatialOverlapMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse> getGetLatestNdviMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetLatestNdvi",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse> getGetLatestNdviMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse> getGetLatestNdviMethod;
    if ((getGetLatestNdviMethod = GeospatialServiceGrpc.getGetLatestNdviMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getGetLatestNdviMethod = GeospatialServiceGrpc.getGetLatestNdviMethod) == null) {
          GeospatialServiceGrpc.getGetLatestNdviMethod = getGetLatestNdviMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetLatestNdvi"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("GetLatestNdvi"))
              .build();
        }
      }
    }
    return getGetLatestNdviMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse> getGetNdviTimeSeriesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetNdviTimeSeries",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse> getGetNdviTimeSeriesMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse> getGetNdviTimeSeriesMethod;
    if ((getGetNdviTimeSeriesMethod = GeospatialServiceGrpc.getGetNdviTimeSeriesMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getGetNdviTimeSeriesMethod = GeospatialServiceGrpc.getGetNdviTimeSeriesMethod) == null) {
          GeospatialServiceGrpc.getGetNdviTimeSeriesMethod = getGetNdviTimeSeriesMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetNdviTimeSeries"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("GetNdviTimeSeries"))
              .build();
        }
      }
    }
    return getGetNdviTimeSeriesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse> getPredictHarvestReadinessMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PredictHarvestReadiness",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse> getPredictHarvestReadinessMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse> getPredictHarvestReadinessMethod;
    if ((getPredictHarvestReadinessMethod = GeospatialServiceGrpc.getPredictHarvestReadinessMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getPredictHarvestReadinessMethod = GeospatialServiceGrpc.getPredictHarvestReadinessMethod) == null) {
          GeospatialServiceGrpc.getPredictHarvestReadinessMethod = getPredictHarvestReadinessMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PredictHarvestReadiness"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("PredictHarvestReadiness"))
              .build();
        }
      }
    }
    return getPredictHarvestReadinessMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse> getGetFarmContextMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetFarmContext",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse> getGetFarmContextMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse> getGetFarmContextMethod;
    if ((getGetFarmContextMethod = GeospatialServiceGrpc.getGetFarmContextMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getGetFarmContextMethod = GeospatialServiceGrpc.getGetFarmContextMethod) == null) {
          GeospatialServiceGrpc.getGetFarmContextMethod = getGetFarmContextMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetFarmContext"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("GetFarmContext"))
              .build();
        }
      }
    }
    return getGetFarmContextMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse> getVerifyFarmBoundaryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "VerifyFarmBoundary",
      requestType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest.class,
      responseType = com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest,
      com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse> getVerifyFarmBoundaryMethod() {
    io.grpc.MethodDescriptor<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse> getVerifyFarmBoundaryMethod;
    if ((getVerifyFarmBoundaryMethod = GeospatialServiceGrpc.getVerifyFarmBoundaryMethod) == null) {
      synchronized (GeospatialServiceGrpc.class) {
        if ((getVerifyFarmBoundaryMethod = GeospatialServiceGrpc.getVerifyFarmBoundaryMethod) == null) {
          GeospatialServiceGrpc.getVerifyFarmBoundaryMethod = getVerifyFarmBoundaryMethod =
              io.grpc.MethodDescriptor.<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest, com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "VerifyFarmBoundary"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GeospatialServiceMethodDescriptorSupplier("VerifyFarmBoundary"))
              .build();
        }
      }
    }
    return getVerifyFarmBoundaryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GeospatialServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GeospatialServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GeospatialServiceStub>() {
        @java.lang.Override
        public GeospatialServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GeospatialServiceStub(channel, callOptions);
        }
      };
    return GeospatialServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GeospatialServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GeospatialServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GeospatialServiceBlockingStub>() {
        @java.lang.Override
        public GeospatialServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GeospatialServiceBlockingStub(channel, callOptions);
        }
      };
    return GeospatialServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GeospatialServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GeospatialServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GeospatialServiceFutureStub>() {
        @java.lang.Override
        public GeospatialServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GeospatialServiceFutureStub(channel, callOptions);
        }
      };
    return GeospatialServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void startMonitoring(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStartMonitoringMethod(), responseObserver);
    }

    /**
     */
    default void registerFarmPolygon(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterFarmPolygonMethod(), responseObserver);
    }

    /**
     */
    default void validatePolygon(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidatePolygonMethod(), responseObserver);
    }

    /**
     */
    default void calculateFarmArea(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCalculateFarmAreaMethod(), responseObserver);
    }

    /**
     */
    default void detectSpatialOverlap(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDetectSpatialOverlapMethod(), responseObserver);
    }

    /**
     */
    default void getLatestNdvi(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetLatestNdviMethod(), responseObserver);
    }

    /**
     */
    default void getNdviTimeSeries(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetNdviTimeSeriesMethod(), responseObserver);
    }

    /**
     */
    default void predictHarvestReadiness(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPredictHarvestReadinessMethod(), responseObserver);
    }

    /**
     */
    default void getFarmContext(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetFarmContextMethod(), responseObserver);
    }

    /**
     */
    default void verifyFarmBoundary(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifyFarmBoundaryMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service GeospatialService.
   */
  public static abstract class GeospatialServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GeospatialServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service GeospatialService.
   */
  public static final class GeospatialServiceStub
      extends io.grpc.stub.AbstractAsyncStub<GeospatialServiceStub> {
    private GeospatialServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GeospatialServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GeospatialServiceStub(channel, callOptions);
    }

    /**
     */
    public void startMonitoring(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStartMonitoringMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void registerFarmPolygon(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterFarmPolygonMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void validatePolygon(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidatePolygonMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void calculateFarmArea(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCalculateFarmAreaMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void detectSpatialOverlap(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDetectSpatialOverlapMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getLatestNdvi(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetLatestNdviMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getNdviTimeSeries(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetNdviTimeSeriesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void predictHarvestReadiness(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPredictHarvestReadinessMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getFarmContext(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetFarmContextMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void verifyFarmBoundary(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifyFarmBoundaryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service GeospatialService.
   */
  public static final class GeospatialServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GeospatialServiceBlockingStub> {
    private GeospatialServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GeospatialServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GeospatialServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse startMonitoring(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStartMonitoringMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse registerFarmPolygon(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterFarmPolygonMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse validatePolygon(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidatePolygonMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse calculateFarmArea(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCalculateFarmAreaMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse detectSpatialOverlap(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDetectSpatialOverlapMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse getLatestNdvi(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetLatestNdviMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse getNdviTimeSeries(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetNdviTimeSeriesMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse predictHarvestReadiness(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPredictHarvestReadinessMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse getFarmContext(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetFarmContextMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse verifyFarmBoundary(com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifyFarmBoundaryMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service GeospatialService.
   */
  public static final class GeospatialServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<GeospatialServiceFutureStub> {
    private GeospatialServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GeospatialServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GeospatialServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse> startMonitoring(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStartMonitoringMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse> registerFarmPolygon(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterFarmPolygonMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse> validatePolygon(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidatePolygonMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse> calculateFarmArea(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCalculateFarmAreaMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse> detectSpatialOverlap(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDetectSpatialOverlapMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse> getLatestNdvi(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetLatestNdviMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse> getNdviTimeSeries(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetNdviTimeSeriesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse> predictHarvestReadiness(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPredictHarvestReadinessMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse> getFarmContext(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetFarmContextMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse> verifyFarmBoundary(
        com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifyFarmBoundaryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_START_MONITORING = 0;
  private static final int METHODID_REGISTER_FARM_POLYGON = 1;
  private static final int METHODID_VALIDATE_POLYGON = 2;
  private static final int METHODID_CALCULATE_FARM_AREA = 3;
  private static final int METHODID_DETECT_SPATIAL_OVERLAP = 4;
  private static final int METHODID_GET_LATEST_NDVI = 5;
  private static final int METHODID_GET_NDVI_TIME_SERIES = 6;
  private static final int METHODID_PREDICT_HARVEST_READINESS = 7;
  private static final int METHODID_GET_FARM_CONTEXT = 8;
  private static final int METHODID_VERIFY_FARM_BOUNDARY = 9;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_START_MONITORING:
          serviceImpl.startMonitoring((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse>) responseObserver);
          break;
        case METHODID_REGISTER_FARM_POLYGON:
          serviceImpl.registerFarmPolygon((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_POLYGON:
          serviceImpl.validatePolygon((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse>) responseObserver);
          break;
        case METHODID_CALCULATE_FARM_AREA:
          serviceImpl.calculateFarmArea((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse>) responseObserver);
          break;
        case METHODID_DETECT_SPATIAL_OVERLAP:
          serviceImpl.detectSpatialOverlap((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse>) responseObserver);
          break;
        case METHODID_GET_LATEST_NDVI:
          serviceImpl.getLatestNdvi((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse>) responseObserver);
          break;
        case METHODID_GET_NDVI_TIME_SERIES:
          serviceImpl.getNdviTimeSeries((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse>) responseObserver);
          break;
        case METHODID_PREDICT_HARVEST_READINESS:
          serviceImpl.predictHarvestReadiness((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse>) responseObserver);
          break;
        case METHODID_GET_FARM_CONTEXT:
          serviceImpl.getFarmContext((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse>) responseObserver);
          break;
        case METHODID_VERIFY_FARM_BOUNDARY:
          serviceImpl.verifyFarmBoundary((com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getStartMonitoringMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BooleanResponse>(
                service, METHODID_START_MONITORING)))
        .addMethod(
          getRegisterFarmPolygonMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.RegisterFarmPolygonResponse>(
                service, METHODID_REGISTER_FARM_POLYGON)))
        .addMethod(
          getValidatePolygonMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.ValidatePolygonResponse>(
                service, METHODID_VALIDATE_POLYGON)))
        .addMethod(
          getCalculateFarmAreaMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.CalculateFarmAreaResponse>(
                service, METHODID_CALCULATE_FARM_AREA)))
        .addMethod(
          getDetectSpatialOverlapMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.DetectSpatialOverlapResponse>(
                service, METHODID_DETECT_SPATIAL_OVERLAP)))
        .addMethod(
          getGetLatestNdviMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviResponse>(
                service, METHODID_GET_LATEST_NDVI)))
        .addMethod(
          getGetNdviTimeSeriesMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.NdviTimeSeriesResponse>(
                service, METHODID_GET_NDVI_TIME_SERIES)))
        .addMethod(
          getPredictHarvestReadinessMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.HarvestReadinessResponse>(
                service, METHODID_PREDICT_HARVEST_READINESS)))
        .addMethod(
          getGetFarmContextMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmIdRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.FarmContextResponse>(
                service, METHODID_GET_FARM_CONTEXT)))
        .addMethod(
          getVerifyFarmBoundaryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.VerifyBoundaryRequest,
              com.agriyield.geospatialservice.grpc.GeospatialServiceProto.BoundaryVerificationResponse>(
                service, METHODID_VERIFY_FARM_BOUNDARY)))
        .build();
  }

  private static abstract class GeospatialServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GeospatialServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.geospatialservice.grpc.GeospatialServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GeospatialService");
    }
  }

  private static final class GeospatialServiceFileDescriptorSupplier
      extends GeospatialServiceBaseDescriptorSupplier {
    GeospatialServiceFileDescriptorSupplier() {}
  }

  private static final class GeospatialServiceMethodDescriptorSupplier
      extends GeospatialServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    GeospatialServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GeospatialServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GeospatialServiceFileDescriptorSupplier())
              .addMethod(getStartMonitoringMethod())
              .addMethod(getRegisterFarmPolygonMethod())
              .addMethod(getValidatePolygonMethod())
              .addMethod(getCalculateFarmAreaMethod())
              .addMethod(getDetectSpatialOverlapMethod())
              .addMethod(getGetLatestNdviMethod())
              .addMethod(getGetNdviTimeSeriesMethod())
              .addMethod(getPredictHarvestReadinessMethod())
              .addMethod(getGetFarmContextMethod())
              .addMethod(getVerifyFarmBoundaryMethod())
              .build();
        }
      }
    }
    return result;
  }
}

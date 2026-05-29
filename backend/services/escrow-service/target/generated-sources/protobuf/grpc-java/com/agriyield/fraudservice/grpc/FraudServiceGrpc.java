package com.agriyield.fraudservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: fraud_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FraudServiceGrpc {

  private FraudServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "fraud.FraudService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.ImageMetadataRequest,
      com.agriyield.fraudservice.grpc.ValidationResponse> getValidateImageMetadataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateImageMetadata",
      requestType = com.agriyield.fraudservice.grpc.ImageMetadataRequest.class,
      responseType = com.agriyield.fraudservice.grpc.ValidationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.ImageMetadataRequest,
      com.agriyield.fraudservice.grpc.ValidationResponse> getValidateImageMetadataMethod() {
    io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.ImageMetadataRequest, com.agriyield.fraudservice.grpc.ValidationResponse> getValidateImageMetadataMethod;
    if ((getValidateImageMetadataMethod = FraudServiceGrpc.getValidateImageMetadataMethod) == null) {
      synchronized (FraudServiceGrpc.class) {
        if ((getValidateImageMetadataMethod = FraudServiceGrpc.getValidateImageMetadataMethod) == null) {
          FraudServiceGrpc.getValidateImageMetadataMethod = getValidateImageMetadataMethod =
              io.grpc.MethodDescriptor.<com.agriyield.fraudservice.grpc.ImageMetadataRequest, com.agriyield.fraudservice.grpc.ValidationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateImageMetadata"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.ImageMetadataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.ValidationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FraudServiceMethodDescriptorSupplier("ValidateImageMetadata"))
              .build();
        }
      }
    }
    return getValidateImageMetadataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.VoucherRedemptionRequest,
      com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse> getValidateVoucherRedemptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateVoucherRedemption",
      requestType = com.agriyield.fraudservice.grpc.VoucherRedemptionRequest.class,
      responseType = com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.VoucherRedemptionRequest,
      com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse> getValidateVoucherRedemptionMethod() {
    io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.VoucherRedemptionRequest, com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse> getValidateVoucherRedemptionMethod;
    if ((getValidateVoucherRedemptionMethod = FraudServiceGrpc.getValidateVoucherRedemptionMethod) == null) {
      synchronized (FraudServiceGrpc.class) {
        if ((getValidateVoucherRedemptionMethod = FraudServiceGrpc.getValidateVoucherRedemptionMethod) == null) {
          FraudServiceGrpc.getValidateVoucherRedemptionMethod = getValidateVoucherRedemptionMethod =
              io.grpc.MethodDescriptor.<com.agriyield.fraudservice.grpc.VoucherRedemptionRequest, com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateVoucherRedemption"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.VoucherRedemptionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FraudServiceMethodDescriptorSupplier("ValidateVoucherRedemption"))
              .build();
        }
      }
    }
    return getValidateVoucherRedemptionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.QrSignatureRequest,
      com.agriyield.fraudservice.grpc.ValidationResponse> getValidateQrSignatureMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateQrSignature",
      requestType = com.agriyield.fraudservice.grpc.QrSignatureRequest.class,
      responseType = com.agriyield.fraudservice.grpc.ValidationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.QrSignatureRequest,
      com.agriyield.fraudservice.grpc.ValidationResponse> getValidateQrSignatureMethod() {
    io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.QrSignatureRequest, com.agriyield.fraudservice.grpc.ValidationResponse> getValidateQrSignatureMethod;
    if ((getValidateQrSignatureMethod = FraudServiceGrpc.getValidateQrSignatureMethod) == null) {
      synchronized (FraudServiceGrpc.class) {
        if ((getValidateQrSignatureMethod = FraudServiceGrpc.getValidateQrSignatureMethod) == null) {
          FraudServiceGrpc.getValidateQrSignatureMethod = getValidateQrSignatureMethod =
              io.grpc.MethodDescriptor.<com.agriyield.fraudservice.grpc.QrSignatureRequest, com.agriyield.fraudservice.grpc.ValidationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateQrSignature"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.QrSignatureRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.ValidationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FraudServiceMethodDescriptorSupplier("ValidateQrSignature"))
              .build();
        }
      }
    }
    return getValidateQrSignatureMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.GpsRequest,
      com.agriyield.fraudservice.grpc.GpsAnomalyResponse> getDetectSuspiciousGpsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DetectSuspiciousGps",
      requestType = com.agriyield.fraudservice.grpc.GpsRequest.class,
      responseType = com.agriyield.fraudservice.grpc.GpsAnomalyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.GpsRequest,
      com.agriyield.fraudservice.grpc.GpsAnomalyResponse> getDetectSuspiciousGpsMethod() {
    io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.GpsRequest, com.agriyield.fraudservice.grpc.GpsAnomalyResponse> getDetectSuspiciousGpsMethod;
    if ((getDetectSuspiciousGpsMethod = FraudServiceGrpc.getDetectSuspiciousGpsMethod) == null) {
      synchronized (FraudServiceGrpc.class) {
        if ((getDetectSuspiciousGpsMethod = FraudServiceGrpc.getDetectSuspiciousGpsMethod) == null) {
          FraudServiceGrpc.getDetectSuspiciousGpsMethod = getDetectSuspiciousGpsMethod =
              io.grpc.MethodDescriptor.<com.agriyield.fraudservice.grpc.GpsRequest, com.agriyield.fraudservice.grpc.GpsAnomalyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DetectSuspiciousGps"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.GpsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.GpsAnomalyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FraudServiceMethodDescriptorSupplier("DetectSuspiciousGps"))
              .build();
        }
      }
    }
    return getDetectSuspiciousGpsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.MerchantEligibilityRequest,
      com.agriyield.fraudservice.grpc.MerchantEligibilityResponse> getValidateMerchantEligibilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateMerchantEligibility",
      requestType = com.agriyield.fraudservice.grpc.MerchantEligibilityRequest.class,
      responseType = com.agriyield.fraudservice.grpc.MerchantEligibilityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.MerchantEligibilityRequest,
      com.agriyield.fraudservice.grpc.MerchantEligibilityResponse> getValidateMerchantEligibilityMethod() {
    io.grpc.MethodDescriptor<com.agriyield.fraudservice.grpc.MerchantEligibilityRequest, com.agriyield.fraudservice.grpc.MerchantEligibilityResponse> getValidateMerchantEligibilityMethod;
    if ((getValidateMerchantEligibilityMethod = FraudServiceGrpc.getValidateMerchantEligibilityMethod) == null) {
      synchronized (FraudServiceGrpc.class) {
        if ((getValidateMerchantEligibilityMethod = FraudServiceGrpc.getValidateMerchantEligibilityMethod) == null) {
          FraudServiceGrpc.getValidateMerchantEligibilityMethod = getValidateMerchantEligibilityMethod =
              io.grpc.MethodDescriptor.<com.agriyield.fraudservice.grpc.MerchantEligibilityRequest, com.agriyield.fraudservice.grpc.MerchantEligibilityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateMerchantEligibility"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.MerchantEligibilityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.fraudservice.grpc.MerchantEligibilityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FraudServiceMethodDescriptorSupplier("ValidateMerchantEligibility"))
              .build();
        }
      }
    }
    return getValidateMerchantEligibilityMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FraudServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FraudServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FraudServiceStub>() {
        @java.lang.Override
        public FraudServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FraudServiceStub(channel, callOptions);
        }
      };
    return FraudServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FraudServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FraudServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FraudServiceBlockingStub>() {
        @java.lang.Override
        public FraudServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FraudServiceBlockingStub(channel, callOptions);
        }
      };
    return FraudServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FraudServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FraudServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FraudServiceFutureStub>() {
        @java.lang.Override
        public FraudServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FraudServiceFutureStub(channel, callOptions);
        }
      };
    return FraudServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * FR-02
     * </pre>
     */
    default void validateImageMetadata(com.agriyield.fraudservice.grpc.ImageMetadataRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.ValidationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateImageMetadataMethod(), responseObserver);
    }

    /**
     * <pre>
     * FR-03
     * </pre>
     */
    default void validateVoucherRedemption(com.agriyield.fraudservice.grpc.VoucherRedemptionRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateVoucherRedemptionMethod(), responseObserver);
    }

    /**
     * <pre>
     * FR-04
     * </pre>
     */
    default void validateQrSignature(com.agriyield.fraudservice.grpc.QrSignatureRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.ValidationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateQrSignatureMethod(), responseObserver);
    }

    /**
     * <pre>
     * FR-05
     * </pre>
     */
    default void detectSuspiciousGps(com.agriyield.fraudservice.grpc.GpsRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.GpsAnomalyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDetectSuspiciousGpsMethod(), responseObserver);
    }

    /**
     * <pre>
     * FR-10
     * </pre>
     */
    default void validateMerchantEligibility(com.agriyield.fraudservice.grpc.MerchantEligibilityRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.MerchantEligibilityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateMerchantEligibilityMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FraudService.
   */
  public static abstract class FraudServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FraudServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FraudService.
   */
  public static final class FraudServiceStub
      extends io.grpc.stub.AbstractAsyncStub<FraudServiceStub> {
    private FraudServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FraudServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FraudServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * FR-02
     * </pre>
     */
    public void validateImageMetadata(com.agriyield.fraudservice.grpc.ImageMetadataRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.ValidationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateImageMetadataMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * FR-03
     * </pre>
     */
    public void validateVoucherRedemption(com.agriyield.fraudservice.grpc.VoucherRedemptionRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateVoucherRedemptionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * FR-04
     * </pre>
     */
    public void validateQrSignature(com.agriyield.fraudservice.grpc.QrSignatureRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.ValidationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateQrSignatureMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * FR-05
     * </pre>
     */
    public void detectSuspiciousGps(com.agriyield.fraudservice.grpc.GpsRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.GpsAnomalyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDetectSuspiciousGpsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * FR-10
     * </pre>
     */
    public void validateMerchantEligibility(com.agriyield.fraudservice.grpc.MerchantEligibilityRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.MerchantEligibilityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateMerchantEligibilityMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FraudService.
   */
  public static final class FraudServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FraudServiceBlockingStub> {
    private FraudServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FraudServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FraudServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * FR-02
     * </pre>
     */
    public com.agriyield.fraudservice.grpc.ValidationResponse validateImageMetadata(com.agriyield.fraudservice.grpc.ImageMetadataRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateImageMetadataMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * FR-03
     * </pre>
     */
    public com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse validateVoucherRedemption(com.agriyield.fraudservice.grpc.VoucherRedemptionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateVoucherRedemptionMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * FR-04
     * </pre>
     */
    public com.agriyield.fraudservice.grpc.ValidationResponse validateQrSignature(com.agriyield.fraudservice.grpc.QrSignatureRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateQrSignatureMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * FR-05
     * </pre>
     */
    public com.agriyield.fraudservice.grpc.GpsAnomalyResponse detectSuspiciousGps(com.agriyield.fraudservice.grpc.GpsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDetectSuspiciousGpsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * FR-10
     * </pre>
     */
    public com.agriyield.fraudservice.grpc.MerchantEligibilityResponse validateMerchantEligibility(com.agriyield.fraudservice.grpc.MerchantEligibilityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateMerchantEligibilityMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FraudService.
   */
  public static final class FraudServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<FraudServiceFutureStub> {
    private FraudServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FraudServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FraudServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * FR-02
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.fraudservice.grpc.ValidationResponse> validateImageMetadata(
        com.agriyield.fraudservice.grpc.ImageMetadataRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateImageMetadataMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * FR-03
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse> validateVoucherRedemption(
        com.agriyield.fraudservice.grpc.VoucherRedemptionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateVoucherRedemptionMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * FR-04
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.fraudservice.grpc.ValidationResponse> validateQrSignature(
        com.agriyield.fraudservice.grpc.QrSignatureRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateQrSignatureMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * FR-05
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.fraudservice.grpc.GpsAnomalyResponse> detectSuspiciousGps(
        com.agriyield.fraudservice.grpc.GpsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDetectSuspiciousGpsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * FR-10
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.fraudservice.grpc.MerchantEligibilityResponse> validateMerchantEligibility(
        com.agriyield.fraudservice.grpc.MerchantEligibilityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateMerchantEligibilityMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VALIDATE_IMAGE_METADATA = 0;
  private static final int METHODID_VALIDATE_VOUCHER_REDEMPTION = 1;
  private static final int METHODID_VALIDATE_QR_SIGNATURE = 2;
  private static final int METHODID_DETECT_SUSPICIOUS_GPS = 3;
  private static final int METHODID_VALIDATE_MERCHANT_ELIGIBILITY = 4;

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
        case METHODID_VALIDATE_IMAGE_METADATA:
          serviceImpl.validateImageMetadata((com.agriyield.fraudservice.grpc.ImageMetadataRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.ValidationResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_VOUCHER_REDEMPTION:
          serviceImpl.validateVoucherRedemption((com.agriyield.fraudservice.grpc.VoucherRedemptionRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_QR_SIGNATURE:
          serviceImpl.validateQrSignature((com.agriyield.fraudservice.grpc.QrSignatureRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.ValidationResponse>) responseObserver);
          break;
        case METHODID_DETECT_SUSPICIOUS_GPS:
          serviceImpl.detectSuspiciousGps((com.agriyield.fraudservice.grpc.GpsRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.GpsAnomalyResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_MERCHANT_ELIGIBILITY:
          serviceImpl.validateMerchantEligibility((com.agriyield.fraudservice.grpc.MerchantEligibilityRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.fraudservice.grpc.MerchantEligibilityResponse>) responseObserver);
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
          getValidateImageMetadataMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.fraudservice.grpc.ImageMetadataRequest,
              com.agriyield.fraudservice.grpc.ValidationResponse>(
                service, METHODID_VALIDATE_IMAGE_METADATA)))
        .addMethod(
          getValidateVoucherRedemptionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.fraudservice.grpc.VoucherRedemptionRequest,
              com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse>(
                service, METHODID_VALIDATE_VOUCHER_REDEMPTION)))
        .addMethod(
          getValidateQrSignatureMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.fraudservice.grpc.QrSignatureRequest,
              com.agriyield.fraudservice.grpc.ValidationResponse>(
                service, METHODID_VALIDATE_QR_SIGNATURE)))
        .addMethod(
          getDetectSuspiciousGpsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.fraudservice.grpc.GpsRequest,
              com.agriyield.fraudservice.grpc.GpsAnomalyResponse>(
                service, METHODID_DETECT_SUSPICIOUS_GPS)))
        .addMethod(
          getValidateMerchantEligibilityMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.fraudservice.grpc.MerchantEligibilityRequest,
              com.agriyield.fraudservice.grpc.MerchantEligibilityResponse>(
                service, METHODID_VALIDATE_MERCHANT_ELIGIBILITY)))
        .build();
  }

  private static abstract class FraudServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FraudServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.fraudservice.grpc.FraudServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FraudService");
    }
  }

  private static final class FraudServiceFileDescriptorSupplier
      extends FraudServiceBaseDescriptorSupplier {
    FraudServiceFileDescriptorSupplier() {}
  }

  private static final class FraudServiceMethodDescriptorSupplier
      extends FraudServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    FraudServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (FraudServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FraudServiceFileDescriptorSupplier())
              .addMethod(getValidateImageMetadataMethod())
              .addMethod(getValidateVoucherRedemptionMethod())
              .addMethod(getValidateQrSignatureMethod())
              .addMethod(getDetectSuspiciousGpsMethod())
              .addMethod(getValidateMerchantEligibilityMethod())
              .build();
        }
      }
    }
    return result;
  }
}

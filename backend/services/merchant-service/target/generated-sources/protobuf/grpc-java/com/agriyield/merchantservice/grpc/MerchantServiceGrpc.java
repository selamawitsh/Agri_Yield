package com.agriyield.merchantservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: merchant_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class MerchantServiceGrpc {

  private MerchantServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.merchantservice.grpc.MerchantService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest,
      com.agriyield.merchantservice.grpc.MerchantCategoriesResponse> getGetMerchantCategoriesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMerchantCategories",
      requestType = com.agriyield.merchantservice.grpc.MerchantIdRequest.class,
      responseType = com.agriyield.merchantservice.grpc.MerchantCategoriesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest,
      com.agriyield.merchantservice.grpc.MerchantCategoriesResponse> getGetMerchantCategoriesMethod() {
    io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest, com.agriyield.merchantservice.grpc.MerchantCategoriesResponse> getGetMerchantCategoriesMethod;
    if ((getGetMerchantCategoriesMethod = MerchantServiceGrpc.getGetMerchantCategoriesMethod) == null) {
      synchronized (MerchantServiceGrpc.class) {
        if ((getGetMerchantCategoriesMethod = MerchantServiceGrpc.getGetMerchantCategoriesMethod) == null) {
          MerchantServiceGrpc.getGetMerchantCategoriesMethod = getGetMerchantCategoriesMethod =
              io.grpc.MethodDescriptor.<com.agriyield.merchantservice.grpc.MerchantIdRequest, com.agriyield.merchantservice.grpc.MerchantCategoriesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetMerchantCategories"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.merchantservice.grpc.MerchantIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.merchantservice.grpc.MerchantCategoriesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MerchantServiceMethodDescriptorSupplier("GetMerchantCategories"))
              .build();
        }
      }
    }
    return getGetMerchantCategoriesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest,
      com.agriyield.merchantservice.grpc.MerchantLocationResponse> getGetMerchantLocationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMerchantLocation",
      requestType = com.agriyield.merchantservice.grpc.MerchantIdRequest.class,
      responseType = com.agriyield.merchantservice.grpc.MerchantLocationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest,
      com.agriyield.merchantservice.grpc.MerchantLocationResponse> getGetMerchantLocationMethod() {
    io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest, com.agriyield.merchantservice.grpc.MerchantLocationResponse> getGetMerchantLocationMethod;
    if ((getGetMerchantLocationMethod = MerchantServiceGrpc.getGetMerchantLocationMethod) == null) {
      synchronized (MerchantServiceGrpc.class) {
        if ((getGetMerchantLocationMethod = MerchantServiceGrpc.getGetMerchantLocationMethod) == null) {
          MerchantServiceGrpc.getGetMerchantLocationMethod = getGetMerchantLocationMethod =
              io.grpc.MethodDescriptor.<com.agriyield.merchantservice.grpc.MerchantIdRequest, com.agriyield.merchantservice.grpc.MerchantLocationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetMerchantLocation"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.merchantservice.grpc.MerchantIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.merchantservice.grpc.MerchantLocationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MerchantServiceMethodDescriptorSupplier("GetMerchantLocation"))
              .build();
        }
      }
    }
    return getGetMerchantLocationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest,
      com.agriyield.merchantservice.grpc.MerchantActiveResponse> getVerifyMerchantActiveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "VerifyMerchantActive",
      requestType = com.agriyield.merchantservice.grpc.MerchantIdRequest.class,
      responseType = com.agriyield.merchantservice.grpc.MerchantActiveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest,
      com.agriyield.merchantservice.grpc.MerchantActiveResponse> getVerifyMerchantActiveMethod() {
    io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.MerchantIdRequest, com.agriyield.merchantservice.grpc.MerchantActiveResponse> getVerifyMerchantActiveMethod;
    if ((getVerifyMerchantActiveMethod = MerchantServiceGrpc.getVerifyMerchantActiveMethod) == null) {
      synchronized (MerchantServiceGrpc.class) {
        if ((getVerifyMerchantActiveMethod = MerchantServiceGrpc.getVerifyMerchantActiveMethod) == null) {
          MerchantServiceGrpc.getVerifyMerchantActiveMethod = getVerifyMerchantActiveMethod =
              io.grpc.MethodDescriptor.<com.agriyield.merchantservice.grpc.MerchantIdRequest, com.agriyield.merchantservice.grpc.MerchantActiveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "VerifyMerchantActive"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.merchantservice.grpc.MerchantIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.merchantservice.grpc.MerchantActiveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MerchantServiceMethodDescriptorSupplier("VerifyMerchantActive"))
              .build();
        }
      }
    }
    return getVerifyMerchantActiveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest,
      com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse> getGetRegionalPriceIndexMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetRegionalPriceIndex",
      requestType = com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest.class,
      responseType = com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest,
      com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse> getGetRegionalPriceIndexMethod() {
    io.grpc.MethodDescriptor<com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest, com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse> getGetRegionalPriceIndexMethod;
    if ((getGetRegionalPriceIndexMethod = MerchantServiceGrpc.getGetRegionalPriceIndexMethod) == null) {
      synchronized (MerchantServiceGrpc.class) {
        if ((getGetRegionalPriceIndexMethod = MerchantServiceGrpc.getGetRegionalPriceIndexMethod) == null) {
          MerchantServiceGrpc.getGetRegionalPriceIndexMethod = getGetRegionalPriceIndexMethod =
              io.grpc.MethodDescriptor.<com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest, com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetRegionalPriceIndex"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MerchantServiceMethodDescriptorSupplier("GetRegionalPriceIndex"))
              .build();
        }
      }
    }
    return getGetRegionalPriceIndexMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MerchantServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MerchantServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MerchantServiceStub>() {
        @java.lang.Override
        public MerchantServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MerchantServiceStub(channel, callOptions);
        }
      };
    return MerchantServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MerchantServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MerchantServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MerchantServiceBlockingStub>() {
        @java.lang.Override
        public MerchantServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MerchantServiceBlockingStub(channel, callOptions);
        }
      };
    return MerchantServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MerchantServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MerchantServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MerchantServiceFutureStub>() {
        @java.lang.Override
        public MerchantServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MerchantServiceFutureStub(channel, callOptions);
        }
      };
    return MerchantServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getMerchantCategories(com.agriyield.merchantservice.grpc.MerchantIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantCategoriesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMerchantCategoriesMethod(), responseObserver);
    }

    /**
     */
    default void getMerchantLocation(com.agriyield.merchantservice.grpc.MerchantIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantLocationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMerchantLocationMethod(), responseObserver);
    }

    /**
     */
    default void verifyMerchantActive(com.agriyield.merchantservice.grpc.MerchantIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantActiveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifyMerchantActiveMethod(), responseObserver);
    }

    /**
     */
    default void getRegionalPriceIndex(com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetRegionalPriceIndexMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service MerchantService.
   */
  public static abstract class MerchantServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return MerchantServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service MerchantService.
   */
  public static final class MerchantServiceStub
      extends io.grpc.stub.AbstractAsyncStub<MerchantServiceStub> {
    private MerchantServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MerchantServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MerchantServiceStub(channel, callOptions);
    }

    /**
     */
    public void getMerchantCategories(com.agriyield.merchantservice.grpc.MerchantIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantCategoriesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMerchantCategoriesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMerchantLocation(com.agriyield.merchantservice.grpc.MerchantIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantLocationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMerchantLocationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void verifyMerchantActive(com.agriyield.merchantservice.grpc.MerchantIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantActiveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifyMerchantActiveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getRegionalPriceIndex(com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetRegionalPriceIndexMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service MerchantService.
   */
  public static final class MerchantServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<MerchantServiceBlockingStub> {
    private MerchantServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MerchantServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MerchantServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.merchantservice.grpc.MerchantCategoriesResponse getMerchantCategories(com.agriyield.merchantservice.grpc.MerchantIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMerchantCategoriesMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.merchantservice.grpc.MerchantLocationResponse getMerchantLocation(com.agriyield.merchantservice.grpc.MerchantIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMerchantLocationMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.merchantservice.grpc.MerchantActiveResponse verifyMerchantActive(com.agriyield.merchantservice.grpc.MerchantIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifyMerchantActiveMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse getRegionalPriceIndex(com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetRegionalPriceIndexMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service MerchantService.
   */
  public static final class MerchantServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<MerchantServiceFutureStub> {
    private MerchantServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MerchantServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MerchantServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.merchantservice.grpc.MerchantCategoriesResponse> getMerchantCategories(
        com.agriyield.merchantservice.grpc.MerchantIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMerchantCategoriesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.merchantservice.grpc.MerchantLocationResponse> getMerchantLocation(
        com.agriyield.merchantservice.grpc.MerchantIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMerchantLocationMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.merchantservice.grpc.MerchantActiveResponse> verifyMerchantActive(
        com.agriyield.merchantservice.grpc.MerchantIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifyMerchantActiveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse> getRegionalPriceIndex(
        com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetRegionalPriceIndexMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_MERCHANT_CATEGORIES = 0;
  private static final int METHODID_GET_MERCHANT_LOCATION = 1;
  private static final int METHODID_VERIFY_MERCHANT_ACTIVE = 2;
  private static final int METHODID_GET_REGIONAL_PRICE_INDEX = 3;

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
        case METHODID_GET_MERCHANT_CATEGORIES:
          serviceImpl.getMerchantCategories((com.agriyield.merchantservice.grpc.MerchantIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantCategoriesResponse>) responseObserver);
          break;
        case METHODID_GET_MERCHANT_LOCATION:
          serviceImpl.getMerchantLocation((com.agriyield.merchantservice.grpc.MerchantIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantLocationResponse>) responseObserver);
          break;
        case METHODID_VERIFY_MERCHANT_ACTIVE:
          serviceImpl.verifyMerchantActive((com.agriyield.merchantservice.grpc.MerchantIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.MerchantActiveResponse>) responseObserver);
          break;
        case METHODID_GET_REGIONAL_PRICE_INDEX:
          serviceImpl.getRegionalPriceIndex((com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse>) responseObserver);
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
          getGetMerchantCategoriesMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.merchantservice.grpc.MerchantIdRequest,
              com.agriyield.merchantservice.grpc.MerchantCategoriesResponse>(
                service, METHODID_GET_MERCHANT_CATEGORIES)))
        .addMethod(
          getGetMerchantLocationMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.merchantservice.grpc.MerchantIdRequest,
              com.agriyield.merchantservice.grpc.MerchantLocationResponse>(
                service, METHODID_GET_MERCHANT_LOCATION)))
        .addMethod(
          getVerifyMerchantActiveMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.merchantservice.grpc.MerchantIdRequest,
              com.agriyield.merchantservice.grpc.MerchantActiveResponse>(
                service, METHODID_VERIFY_MERCHANT_ACTIVE)))
        .addMethod(
          getGetRegionalPriceIndexMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest,
              com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse>(
                service, METHODID_GET_REGIONAL_PRICE_INDEX)))
        .build();
  }

  private static abstract class MerchantServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MerchantServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.merchantservice.grpc.MerchantProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MerchantService");
    }
  }

  private static final class MerchantServiceFileDescriptorSupplier
      extends MerchantServiceBaseDescriptorSupplier {
    MerchantServiceFileDescriptorSupplier() {}
  }

  private static final class MerchantServiceMethodDescriptorSupplier
      extends MerchantServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    MerchantServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (MerchantServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MerchantServiceFileDescriptorSupplier())
              .addMethod(getGetMerchantCategoriesMethod())
              .addMethod(getGetMerchantLocationMethod())
              .addMethod(getVerifyMerchantActiveMethod())
              .addMethod(getGetRegionalPriceIndexMethod())
              .build();
        }
      }
    }
    return result;
  }
}

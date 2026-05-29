package com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: fayda_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FaydaServiceGrpc {

  private FaydaServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.fayda.FaydaService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest,
      com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse> getVerifyIdentityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "VerifyIdentity",
      requestType = com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest.class,
      responseType = com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest,
      com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse> getVerifyIdentityMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest, com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse> getVerifyIdentityMethod;
    if ((getVerifyIdentityMethod = FaydaServiceGrpc.getVerifyIdentityMethod) == null) {
      synchronized (FaydaServiceGrpc.class) {
        if ((getVerifyIdentityMethod = FaydaServiceGrpc.getVerifyIdentityMethod) == null) {
          FaydaServiceGrpc.getVerifyIdentityMethod = getVerifyIdentityMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest, com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "VerifyIdentity"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FaydaServiceMethodDescriptorSupplier("VerifyIdentity"))
              .build();
        }
      }
    }
    return getVerifyIdentityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest,
      com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse> getPullKycDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PullKycData",
      requestType = com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest.class,
      responseType = com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest,
      com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse> getPullKycDataMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest, com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse> getPullKycDataMethod;
    if ((getPullKycDataMethod = FaydaServiceGrpc.getPullKycDataMethod) == null) {
      synchronized (FaydaServiceGrpc.class) {
        if ((getPullKycDataMethod = FaydaServiceGrpc.getPullKycDataMethod) == null) {
          FaydaServiceGrpc.getPullKycDataMethod = getPullKycDataMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest, com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PullKycData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FaydaServiceMethodDescriptorSupplier("PullKycData"))
              .build();
        }
      }
    }
    return getPullKycDataMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FaydaServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FaydaServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FaydaServiceStub>() {
        @java.lang.Override
        public FaydaServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FaydaServiceStub(channel, callOptions);
        }
      };
    return FaydaServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FaydaServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FaydaServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FaydaServiceBlockingStub>() {
        @java.lang.Override
        public FaydaServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FaydaServiceBlockingStub(channel, callOptions);
        }
      };
    return FaydaServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FaydaServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FaydaServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FaydaServiceFutureStub>() {
        @java.lang.Override
        public FaydaServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FaydaServiceFutureStub(channel, callOptions);
        }
      };
    return FaydaServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void verifyIdentity(com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifyIdentityMethod(), responseObserver);
    }

    /**
     */
    default void pullKycData(com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPullKycDataMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FaydaService.
   */
  public static abstract class FaydaServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FaydaServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FaydaService.
   */
  public static final class FaydaServiceStub
      extends io.grpc.stub.AbstractAsyncStub<FaydaServiceStub> {
    private FaydaServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FaydaServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FaydaServiceStub(channel, callOptions);
    }

    /**
     */
    public void verifyIdentity(com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifyIdentityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void pullKycData(com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPullKycDataMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FaydaService.
   */
  public static final class FaydaServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FaydaServiceBlockingStub> {
    private FaydaServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FaydaServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FaydaServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse verifyIdentity(com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifyIdentityMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse pullKycData(com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPullKycDataMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FaydaService.
   */
  public static final class FaydaServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<FaydaServiceFutureStub> {
    private FaydaServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FaydaServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FaydaServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse> verifyIdentity(
        com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifyIdentityMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse> pullKycData(
        com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPullKycDataMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VERIFY_IDENTITY = 0;
  private static final int METHODID_PULL_KYC_DATA = 1;

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
        case METHODID_VERIFY_IDENTITY:
          serviceImpl.verifyIdentity((com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse>) responseObserver);
          break;
        case METHODID_PULL_KYC_DATA:
          serviceImpl.pullKycData((com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse>) responseObserver);
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
          getVerifyIdentityMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityRequest,
              com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.VerifyIdentityResponse>(
                service, METHODID_VERIFY_IDENTITY)))
        .addMethod(
          getPullKycDataMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataRequest,
              com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.PullKycDataResponse>(
                service, METHODID_PULL_KYC_DATA)))
        .build();
  }

  private static abstract class FaydaServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FaydaServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc.FaydaServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FaydaService");
    }
  }

  private static final class FaydaServiceFileDescriptorSupplier
      extends FaydaServiceBaseDescriptorSupplier {
    FaydaServiceFileDescriptorSupplier() {}
  }

  private static final class FaydaServiceMethodDescriptorSupplier
      extends FaydaServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    FaydaServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (FaydaServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FaydaServiceFileDescriptorSupplier())
              .addMethod(getVerifyIdentityMethod())
              .addMethod(getPullKycDataMethod())
              .build();
        }
      }
    }
    return result;
  }
}

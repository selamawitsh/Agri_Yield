package com.agriyield.farmservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: farm_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FarmServiceGrpc {

  private FarmServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.farmservice.grpc.FarmService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest,
      com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse> getGetFarmByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetFarmById",
      requestType = com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest,
      com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse> getGetFarmByIdMethod() {
    io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest, com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse> getGetFarmByIdMethod;
    if ((getGetFarmByIdMethod = FarmServiceGrpc.getGetFarmByIdMethod) == null) {
      synchronized (FarmServiceGrpc.class) {
        if ((getGetFarmByIdMethod = FarmServiceGrpc.getGetFarmByIdMethod) == null) {
          FarmServiceGrpc.getGetFarmByIdMethod = getGetFarmByIdMethod =
              io.grpc.MethodDescriptor.<com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest, com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetFarmById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FarmServiceMethodDescriptorSupplier("GetFarmById"))
              .build();
        }
      }
    }
    return getGetFarmByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest,
      com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse> getGetFarmContextMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetFarmContext",
      requestType = com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest,
      com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse> getGetFarmContextMethod() {
    io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest, com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse> getGetFarmContextMethod;
    if ((getGetFarmContextMethod = FarmServiceGrpc.getGetFarmContextMethod) == null) {
      synchronized (FarmServiceGrpc.class) {
        if ((getGetFarmContextMethod = FarmServiceGrpc.getGetFarmContextMethod) == null) {
          FarmServiceGrpc.getGetFarmContextMethod = getGetFarmContextMethod =
              io.grpc.MethodDescriptor.<com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest, com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetFarmContext"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FarmServiceMethodDescriptorSupplier("GetFarmContext"))
              .build();
        }
      }
    }
    return getGetFarmContextMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest,
      com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse> getVerifyFarmBoundaryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "VerifyFarmBoundary",
      requestType = com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest.class,
      responseType = com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest,
      com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse> getVerifyFarmBoundaryMethod() {
    io.grpc.MethodDescriptor<com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest, com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse> getVerifyFarmBoundaryMethod;
    if ((getVerifyFarmBoundaryMethod = FarmServiceGrpc.getVerifyFarmBoundaryMethod) == null) {
      synchronized (FarmServiceGrpc.class) {
        if ((getVerifyFarmBoundaryMethod = FarmServiceGrpc.getVerifyFarmBoundaryMethod) == null) {
          FarmServiceGrpc.getVerifyFarmBoundaryMethod = getVerifyFarmBoundaryMethod =
              io.grpc.MethodDescriptor.<com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest, com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "VerifyFarmBoundary"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FarmServiceMethodDescriptorSupplier("VerifyFarmBoundary"))
              .build();
        }
      }
    }
    return getVerifyFarmBoundaryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FarmServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FarmServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FarmServiceStub>() {
        @java.lang.Override
        public FarmServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FarmServiceStub(channel, callOptions);
        }
      };
    return FarmServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FarmServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FarmServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FarmServiceBlockingStub>() {
        @java.lang.Override
        public FarmServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FarmServiceBlockingStub(channel, callOptions);
        }
      };
    return FarmServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FarmServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FarmServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FarmServiceFutureStub>() {
        @java.lang.Override
        public FarmServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FarmServiceFutureStub(channel, callOptions);
        }
      };
    return FarmServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getFarmById(com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetFarmByIdMethod(), responseObserver);
    }

    /**
     */
    default void getFarmContext(com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetFarmContextMethod(), responseObserver);
    }

    /**
     */
    default void verifyFarmBoundary(com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifyFarmBoundaryMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FarmService.
   */
  public static abstract class FarmServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FarmServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FarmService.
   */
  public static final class FarmServiceStub
      extends io.grpc.stub.AbstractAsyncStub<FarmServiceStub> {
    private FarmServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FarmServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FarmServiceStub(channel, callOptions);
    }

    /**
     */
    public void getFarmById(com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetFarmByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getFarmContext(com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetFarmContextMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void verifyFarmBoundary(com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifyFarmBoundaryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FarmService.
   */
  public static final class FarmServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FarmServiceBlockingStub> {
    private FarmServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FarmServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FarmServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse getFarmById(com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetFarmByIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse getFarmContext(com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetFarmContextMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse verifyFarmBoundary(com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifyFarmBoundaryMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FarmService.
   */
  public static final class FarmServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<FarmServiceFutureStub> {
    private FarmServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FarmServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FarmServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse> getFarmById(
        com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetFarmByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse> getFarmContext(
        com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetFarmContextMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse> verifyFarmBoundary(
        com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifyFarmBoundaryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_FARM_BY_ID = 0;
  private static final int METHODID_GET_FARM_CONTEXT = 1;
  private static final int METHODID_VERIFY_FARM_BOUNDARY = 2;

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
        case METHODID_GET_FARM_BY_ID:
          serviceImpl.getFarmById((com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse>) responseObserver);
          break;
        case METHODID_GET_FARM_CONTEXT:
          serviceImpl.getFarmContext((com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse>) responseObserver);
          break;
        case METHODID_VERIFY_FARM_BOUNDARY:
          serviceImpl.verifyFarmBoundary((com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse>) responseObserver);
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
          getGetFarmByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest,
              com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse>(
                service, METHODID_GET_FARM_BY_ID)))
        .addMethod(
          getGetFarmContextMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest,
              com.agriyield.farmservice.grpc.FarmServiceProto.FarmContextResponse>(
                service, METHODID_GET_FARM_CONTEXT)))
        .addMethod(
          getVerifyFarmBoundaryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.farmservice.grpc.FarmServiceProto.VerifyBoundaryRequest,
              com.agriyield.farmservice.grpc.FarmServiceProto.BoundaryVerificationResponse>(
                service, METHODID_VERIFY_FARM_BOUNDARY)))
        .build();
  }

  private static abstract class FarmServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FarmServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.farmservice.grpc.FarmServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FarmService");
    }
  }

  private static final class FarmServiceFileDescriptorSupplier
      extends FarmServiceBaseDescriptorSupplier {
    FarmServiceFileDescriptorSupplier() {}
  }

  private static final class FarmServiceMethodDescriptorSupplier
      extends FarmServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    FarmServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (FarmServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FarmServiceFileDescriptorSupplier())
              .addMethod(getGetFarmByIdMethod())
              .addMethod(getGetFarmContextMethod())
              .addMethod(getVerifyFarmBoundaryMethod())
              .build();
        }
      }
    }
    return result;
  }
}

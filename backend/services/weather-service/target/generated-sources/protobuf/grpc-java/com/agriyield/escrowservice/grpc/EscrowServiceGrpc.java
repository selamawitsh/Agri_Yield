package com.agriyield.escrowservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: escrow_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class EscrowServiceGrpc {

  private EscrowServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.escrowservice.grpc.EscrowService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.CreateEscrowRequest,
      com.agriyield.escrowservice.grpc.EscrowResponse> getCreateAndLockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateAndLock",
      requestType = com.agriyield.escrowservice.grpc.CreateEscrowRequest.class,
      responseType = com.agriyield.escrowservice.grpc.EscrowResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.CreateEscrowRequest,
      com.agriyield.escrowservice.grpc.EscrowResponse> getCreateAndLockMethod() {
    io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.CreateEscrowRequest, com.agriyield.escrowservice.grpc.EscrowResponse> getCreateAndLockMethod;
    if ((getCreateAndLockMethod = EscrowServiceGrpc.getCreateAndLockMethod) == null) {
      synchronized (EscrowServiceGrpc.class) {
        if ((getCreateAndLockMethod = EscrowServiceGrpc.getCreateAndLockMethod) == null) {
          EscrowServiceGrpc.getCreateAndLockMethod = getCreateAndLockMethod =
              io.grpc.MethodDescriptor.<com.agriyield.escrowservice.grpc.CreateEscrowRequest, com.agriyield.escrowservice.grpc.EscrowResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateAndLock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.escrowservice.grpc.CreateEscrowRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.escrowservice.grpc.EscrowResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EscrowServiceMethodDescriptorSupplier("CreateAndLock"))
              .build();
        }
      }
    }
    return getCreateAndLockMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.ReleasePartialRequest,
      com.agriyield.escrowservice.grpc.ReleaseResponse> getReleasePartialMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReleasePartial",
      requestType = com.agriyield.escrowservice.grpc.ReleasePartialRequest.class,
      responseType = com.agriyield.escrowservice.grpc.ReleaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.ReleasePartialRequest,
      com.agriyield.escrowservice.grpc.ReleaseResponse> getReleasePartialMethod() {
    io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.ReleasePartialRequest, com.agriyield.escrowservice.grpc.ReleaseResponse> getReleasePartialMethod;
    if ((getReleasePartialMethod = EscrowServiceGrpc.getReleasePartialMethod) == null) {
      synchronized (EscrowServiceGrpc.class) {
        if ((getReleasePartialMethod = EscrowServiceGrpc.getReleasePartialMethod) == null) {
          EscrowServiceGrpc.getReleasePartialMethod = getReleasePartialMethod =
              io.grpc.MethodDescriptor.<com.agriyield.escrowservice.grpc.ReleasePartialRequest, com.agriyield.escrowservice.grpc.ReleaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReleasePartial"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.escrowservice.grpc.ReleasePartialRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.escrowservice.grpc.ReleaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EscrowServiceMethodDescriptorSupplier("ReleasePartial"))
              .build();
        }
      }
    }
    return getReleasePartialMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.CancelEscrowRequest,
      com.agriyield.escrowservice.grpc.EscrowResponse> getCancelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Cancel",
      requestType = com.agriyield.escrowservice.grpc.CancelEscrowRequest.class,
      responseType = com.agriyield.escrowservice.grpc.EscrowResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.CancelEscrowRequest,
      com.agriyield.escrowservice.grpc.EscrowResponse> getCancelMethod() {
    io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.CancelEscrowRequest, com.agriyield.escrowservice.grpc.EscrowResponse> getCancelMethod;
    if ((getCancelMethod = EscrowServiceGrpc.getCancelMethod) == null) {
      synchronized (EscrowServiceGrpc.class) {
        if ((getCancelMethod = EscrowServiceGrpc.getCancelMethod) == null) {
          EscrowServiceGrpc.getCancelMethod = getCancelMethod =
              io.grpc.MethodDescriptor.<com.agriyield.escrowservice.grpc.CancelEscrowRequest, com.agriyield.escrowservice.grpc.EscrowResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Cancel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.escrowservice.grpc.CancelEscrowRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.escrowservice.grpc.EscrowResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EscrowServiceMethodDescriptorSupplier("Cancel"))
              .build();
        }
      }
    }
    return getCancelMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.GetEscrowRequest,
      com.agriyield.escrowservice.grpc.EscrowResponse> getGetByInvestmentIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetByInvestmentId",
      requestType = com.agriyield.escrowservice.grpc.GetEscrowRequest.class,
      responseType = com.agriyield.escrowservice.grpc.EscrowResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.GetEscrowRequest,
      com.agriyield.escrowservice.grpc.EscrowResponse> getGetByInvestmentIdMethod() {
    io.grpc.MethodDescriptor<com.agriyield.escrowservice.grpc.GetEscrowRequest, com.agriyield.escrowservice.grpc.EscrowResponse> getGetByInvestmentIdMethod;
    if ((getGetByInvestmentIdMethod = EscrowServiceGrpc.getGetByInvestmentIdMethod) == null) {
      synchronized (EscrowServiceGrpc.class) {
        if ((getGetByInvestmentIdMethod = EscrowServiceGrpc.getGetByInvestmentIdMethod) == null) {
          EscrowServiceGrpc.getGetByInvestmentIdMethod = getGetByInvestmentIdMethod =
              io.grpc.MethodDescriptor.<com.agriyield.escrowservice.grpc.GetEscrowRequest, com.agriyield.escrowservice.grpc.EscrowResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetByInvestmentId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.escrowservice.grpc.GetEscrowRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.escrowservice.grpc.EscrowResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EscrowServiceMethodDescriptorSupplier("GetByInvestmentId"))
              .build();
        }
      }
    }
    return getGetByInvestmentIdMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static EscrowServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EscrowServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EscrowServiceStub>() {
        @java.lang.Override
        public EscrowServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EscrowServiceStub(channel, callOptions);
        }
      };
    return EscrowServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static EscrowServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EscrowServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EscrowServiceBlockingStub>() {
        @java.lang.Override
        public EscrowServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EscrowServiceBlockingStub(channel, callOptions);
        }
      };
    return EscrowServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static EscrowServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EscrowServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EscrowServiceFutureStub>() {
        @java.lang.Override
        public EscrowServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EscrowServiceFutureStub(channel, callOptions);
        }
      };
    return EscrowServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void createAndLock(com.agriyield.escrowservice.grpc.CreateEscrowRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateAndLockMethod(), responseObserver);
    }

    /**
     */
    default void releasePartial(com.agriyield.escrowservice.grpc.ReleasePartialRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.ReleaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReleasePartialMethod(), responseObserver);
    }

    /**
     */
    default void cancel(com.agriyield.escrowservice.grpc.CancelEscrowRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelMethod(), responseObserver);
    }

    /**
     */
    default void getByInvestmentId(com.agriyield.escrowservice.grpc.GetEscrowRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetByInvestmentIdMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service EscrowService.
   */
  public static abstract class EscrowServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return EscrowServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service EscrowService.
   */
  public static final class EscrowServiceStub
      extends io.grpc.stub.AbstractAsyncStub<EscrowServiceStub> {
    private EscrowServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EscrowServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EscrowServiceStub(channel, callOptions);
    }

    /**
     */
    public void createAndLock(com.agriyield.escrowservice.grpc.CreateEscrowRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateAndLockMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void releasePartial(com.agriyield.escrowservice.grpc.ReleasePartialRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.ReleaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReleasePartialMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void cancel(com.agriyield.escrowservice.grpc.CancelEscrowRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getByInvestmentId(com.agriyield.escrowservice.grpc.GetEscrowRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetByInvestmentIdMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service EscrowService.
   */
  public static final class EscrowServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<EscrowServiceBlockingStub> {
    private EscrowServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EscrowServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EscrowServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.escrowservice.grpc.EscrowResponse createAndLock(com.agriyield.escrowservice.grpc.CreateEscrowRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateAndLockMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.escrowservice.grpc.ReleaseResponse releasePartial(com.agriyield.escrowservice.grpc.ReleasePartialRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReleasePartialMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.escrowservice.grpc.EscrowResponse cancel(com.agriyield.escrowservice.grpc.CancelEscrowRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.escrowservice.grpc.EscrowResponse getByInvestmentId(com.agriyield.escrowservice.grpc.GetEscrowRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetByInvestmentIdMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service EscrowService.
   */
  public static final class EscrowServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<EscrowServiceFutureStub> {
    private EscrowServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EscrowServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EscrowServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.escrowservice.grpc.EscrowResponse> createAndLock(
        com.agriyield.escrowservice.grpc.CreateEscrowRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateAndLockMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.escrowservice.grpc.ReleaseResponse> releasePartial(
        com.agriyield.escrowservice.grpc.ReleasePartialRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReleasePartialMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.escrowservice.grpc.EscrowResponse> cancel(
        com.agriyield.escrowservice.grpc.CancelEscrowRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.escrowservice.grpc.EscrowResponse> getByInvestmentId(
        com.agriyield.escrowservice.grpc.GetEscrowRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetByInvestmentIdMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_AND_LOCK = 0;
  private static final int METHODID_RELEASE_PARTIAL = 1;
  private static final int METHODID_CANCEL = 2;
  private static final int METHODID_GET_BY_INVESTMENT_ID = 3;

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
        case METHODID_CREATE_AND_LOCK:
          serviceImpl.createAndLock((com.agriyield.escrowservice.grpc.CreateEscrowRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse>) responseObserver);
          break;
        case METHODID_RELEASE_PARTIAL:
          serviceImpl.releasePartial((com.agriyield.escrowservice.grpc.ReleasePartialRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.ReleaseResponse>) responseObserver);
          break;
        case METHODID_CANCEL:
          serviceImpl.cancel((com.agriyield.escrowservice.grpc.CancelEscrowRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse>) responseObserver);
          break;
        case METHODID_GET_BY_INVESTMENT_ID:
          serviceImpl.getByInvestmentId((com.agriyield.escrowservice.grpc.GetEscrowRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.escrowservice.grpc.EscrowResponse>) responseObserver);
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
          getCreateAndLockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.escrowservice.grpc.CreateEscrowRequest,
              com.agriyield.escrowservice.grpc.EscrowResponse>(
                service, METHODID_CREATE_AND_LOCK)))
        .addMethod(
          getReleasePartialMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.escrowservice.grpc.ReleasePartialRequest,
              com.agriyield.escrowservice.grpc.ReleaseResponse>(
                service, METHODID_RELEASE_PARTIAL)))
        .addMethod(
          getCancelMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.escrowservice.grpc.CancelEscrowRequest,
              com.agriyield.escrowservice.grpc.EscrowResponse>(
                service, METHODID_CANCEL)))
        .addMethod(
          getGetByInvestmentIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.escrowservice.grpc.GetEscrowRequest,
              com.agriyield.escrowservice.grpc.EscrowResponse>(
                service, METHODID_GET_BY_INVESTMENT_ID)))
        .build();
  }

  private static abstract class EscrowServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    EscrowServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.escrowservice.grpc.EscrowServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("EscrowService");
    }
  }

  private static final class EscrowServiceFileDescriptorSupplier
      extends EscrowServiceBaseDescriptorSupplier {
    EscrowServiceFileDescriptorSupplier() {}
  }

  private static final class EscrowServiceMethodDescriptorSupplier
      extends EscrowServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    EscrowServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (EscrowServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new EscrowServiceFileDescriptorSupplier())
              .addMethod(getCreateAndLockMethod())
              .addMethod(getReleasePartialMethod())
              .addMethod(getCancelMethod())
              .addMethod(getGetByInvestmentIdMethod())
              .build();
        }
      }
    }
    return result;
  }
}

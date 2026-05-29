package com.agriyield.voucherservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: voucher_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class VoucherServiceGrpc {

  private VoucherServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.voucherservice.grpc.VoucherService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest,
      com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse> getGetVoucherByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetVoucherById",
      requestType = com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest.class,
      responseType = com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest,
      com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse> getGetVoucherByIdMethod() {
    io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest, com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse> getGetVoucherByIdMethod;
    if ((getGetVoucherByIdMethod = VoucherServiceGrpc.getGetVoucherByIdMethod) == null) {
      synchronized (VoucherServiceGrpc.class) {
        if ((getGetVoucherByIdMethod = VoucherServiceGrpc.getGetVoucherByIdMethod) == null) {
          VoucherServiceGrpc.getGetVoucherByIdMethod = getGetVoucherByIdMethod =
              io.grpc.MethodDescriptor.<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest, com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetVoucherById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VoucherServiceMethodDescriptorSupplier("GetVoucherById"))
              .build();
        }
      }
    }
    return getGetVoucherByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest,
      com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse> getGetVouchersByFarmIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetVouchersByFarmId",
      requestType = com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest,
      com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse> getGetVouchersByFarmIdMethod() {
    io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest, com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse> getGetVouchersByFarmIdMethod;
    if ((getGetVouchersByFarmIdMethod = VoucherServiceGrpc.getGetVouchersByFarmIdMethod) == null) {
      synchronized (VoucherServiceGrpc.class) {
        if ((getGetVouchersByFarmIdMethod = VoucherServiceGrpc.getGetVouchersByFarmIdMethod) == null) {
          VoucherServiceGrpc.getGetVouchersByFarmIdMethod = getGetVouchersByFarmIdMethod =
              io.grpc.MethodDescriptor.<com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest, com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetVouchersByFarmId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VoucherServiceMethodDescriptorSupplier("GetVouchersByFarmId"))
              .build();
        }
      }
    }
    return getGetVouchersByFarmIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest,
      com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse> getVerifyVoucherMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "VerifyVoucher",
      requestType = com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest.class,
      responseType = com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest,
      com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse> getVerifyVoucherMethod() {
    io.grpc.MethodDescriptor<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest, com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse> getVerifyVoucherMethod;
    if ((getVerifyVoucherMethod = VoucherServiceGrpc.getVerifyVoucherMethod) == null) {
      synchronized (VoucherServiceGrpc.class) {
        if ((getVerifyVoucherMethod = VoucherServiceGrpc.getVerifyVoucherMethod) == null) {
          VoucherServiceGrpc.getVerifyVoucherMethod = getVerifyVoucherMethod =
              io.grpc.MethodDescriptor.<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest, com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "VerifyVoucher"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VoucherServiceMethodDescriptorSupplier("VerifyVoucher"))
              .build();
        }
      }
    }
    return getVerifyVoucherMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VoucherServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VoucherServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VoucherServiceStub>() {
        @java.lang.Override
        public VoucherServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VoucherServiceStub(channel, callOptions);
        }
      };
    return VoucherServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VoucherServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VoucherServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VoucherServiceBlockingStub>() {
        @java.lang.Override
        public VoucherServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VoucherServiceBlockingStub(channel, callOptions);
        }
      };
    return VoucherServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VoucherServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VoucherServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VoucherServiceFutureStub>() {
        @java.lang.Override
        public VoucherServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VoucherServiceFutureStub(channel, callOptions);
        }
      };
    return VoucherServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getVoucherById(com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetVoucherByIdMethod(), responseObserver);
    }

    /**
     */
    default void getVouchersByFarmId(com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetVouchersByFarmIdMethod(), responseObserver);
    }

    /**
     */
    default void verifyVoucher(com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifyVoucherMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service VoucherService.
   */
  public static abstract class VoucherServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return VoucherServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service VoucherService.
   */
  public static final class VoucherServiceStub
      extends io.grpc.stub.AbstractAsyncStub<VoucherServiceStub> {
    private VoucherServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VoucherServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VoucherServiceStub(channel, callOptions);
    }

    /**
     */
    public void getVoucherById(com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetVoucherByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getVouchersByFarmId(com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetVouchersByFarmIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void verifyVoucher(com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifyVoucherMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service VoucherService.
   */
  public static final class VoucherServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<VoucherServiceBlockingStub> {
    private VoucherServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VoucherServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VoucherServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse getVoucherById(com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetVoucherByIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse getVouchersByFarmId(com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetVouchersByFarmIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse verifyVoucher(com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifyVoucherMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service VoucherService.
   */
  public static final class VoucherServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<VoucherServiceFutureStub> {
    private VoucherServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VoucherServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VoucherServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse> getVoucherById(
        com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetVoucherByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse> getVouchersByFarmId(
        com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetVouchersByFarmIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse> verifyVoucher(
        com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifyVoucherMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_VOUCHER_BY_ID = 0;
  private static final int METHODID_GET_VOUCHERS_BY_FARM_ID = 1;
  private static final int METHODID_VERIFY_VOUCHER = 2;

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
        case METHODID_GET_VOUCHER_BY_ID:
          serviceImpl.getVoucherById((com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse>) responseObserver);
          break;
        case METHODID_GET_VOUCHERS_BY_FARM_ID:
          serviceImpl.getVouchersByFarmId((com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse>) responseObserver);
          break;
        case METHODID_VERIFY_VOUCHER:
          serviceImpl.verifyVoucher((com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse>) responseObserver);
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
          getGetVoucherByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherIdRequest,
              com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherResponse>(
                service, METHODID_GET_VOUCHER_BY_ID)))
        .addMethod(
          getGetVouchersByFarmIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.voucherservice.grpc.VoucherServiceProto.FarmIdRequest,
              com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherListResponse>(
                service, METHODID_GET_VOUCHERS_BY_FARM_ID)))
        .addMethod(
          getVerifyVoucherMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherCodeRequest,
              com.agriyield.voucherservice.grpc.VoucherServiceProto.VoucherVerifyResponse>(
                service, METHODID_VERIFY_VOUCHER)))
        .build();
  }

  private static abstract class VoucherServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    VoucherServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.voucherservice.grpc.VoucherServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("VoucherService");
    }
  }

  private static final class VoucherServiceFileDescriptorSupplier
      extends VoucherServiceBaseDescriptorSupplier {
    VoucherServiceFileDescriptorSupplier() {}
  }

  private static final class VoucherServiceMethodDescriptorSupplier
      extends VoucherServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    VoucherServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (VoucherServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VoucherServiceFileDescriptorSupplier())
              .addMethod(getGetVoucherByIdMethod())
              .addMethod(getGetVouchersByFarmIdMethod())
              .addMethod(getVerifyVoucherMethod())
              .build();
        }
      }
    }
    return result;
  }
}

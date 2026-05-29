package com.agriyield.investmentservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: investment_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InvestmentServiceGrpc {

  private InvestmentServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.investmentservice.grpc.InvestmentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest,
      com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> getGetInvestmentByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetInvestmentById",
      requestType = com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest.class,
      responseType = com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest,
      com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> getGetInvestmentByIdMethod() {
    io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest, com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> getGetInvestmentByIdMethod;
    if ((getGetInvestmentByIdMethod = InvestmentServiceGrpc.getGetInvestmentByIdMethod) == null) {
      synchronized (InvestmentServiceGrpc.class) {
        if ((getGetInvestmentByIdMethod = InvestmentServiceGrpc.getGetInvestmentByIdMethod) == null) {
          InvestmentServiceGrpc.getGetInvestmentByIdMethod = getGetInvestmentByIdMethod =
              io.grpc.MethodDescriptor.<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest, com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetInvestmentById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InvestmentServiceMethodDescriptorSupplier("GetInvestmentById"))
              .build();
        }
      }
    }
    return getGetInvestmentByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest,
      com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> getGetInvestmentByFarmIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetInvestmentByFarmId",
      requestType = com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest,
      com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> getGetInvestmentByFarmIdMethod() {
    io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest, com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> getGetInvestmentByFarmIdMethod;
    if ((getGetInvestmentByFarmIdMethod = InvestmentServiceGrpc.getGetInvestmentByFarmIdMethod) == null) {
      synchronized (InvestmentServiceGrpc.class) {
        if ((getGetInvestmentByFarmIdMethod = InvestmentServiceGrpc.getGetInvestmentByFarmIdMethod) == null) {
          InvestmentServiceGrpc.getGetInvestmentByFarmIdMethod = getGetInvestmentByFarmIdMethod =
              io.grpc.MethodDescriptor.<com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest, com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetInvestmentByFarmId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InvestmentServiceMethodDescriptorSupplier("GetInvestmentByFarmId"))
              .build();
        }
      }
    }
    return getGetInvestmentByFarmIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest,
      com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse> getVerifyInvestmentFundedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "VerifyInvestmentFunded",
      requestType = com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest.class,
      responseType = com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest,
      com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse> getVerifyInvestmentFundedMethod() {
    io.grpc.MethodDescriptor<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest, com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse> getVerifyInvestmentFundedMethod;
    if ((getVerifyInvestmentFundedMethod = InvestmentServiceGrpc.getVerifyInvestmentFundedMethod) == null) {
      synchronized (InvestmentServiceGrpc.class) {
        if ((getVerifyInvestmentFundedMethod = InvestmentServiceGrpc.getVerifyInvestmentFundedMethod) == null) {
          InvestmentServiceGrpc.getVerifyInvestmentFundedMethod = getVerifyInvestmentFundedMethod =
              io.grpc.MethodDescriptor.<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest, com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "VerifyInvestmentFunded"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InvestmentServiceMethodDescriptorSupplier("VerifyInvestmentFunded"))
              .build();
        }
      }
    }
    return getVerifyInvestmentFundedMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InvestmentServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InvestmentServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InvestmentServiceStub>() {
        @java.lang.Override
        public InvestmentServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InvestmentServiceStub(channel, callOptions);
        }
      };
    return InvestmentServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InvestmentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InvestmentServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InvestmentServiceBlockingStub>() {
        @java.lang.Override
        public InvestmentServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InvestmentServiceBlockingStub(channel, callOptions);
        }
      };
    return InvestmentServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InvestmentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InvestmentServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InvestmentServiceFutureStub>() {
        @java.lang.Override
        public InvestmentServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InvestmentServiceFutureStub(channel, callOptions);
        }
      };
    return InvestmentServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getInvestmentById(com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetInvestmentByIdMethod(), responseObserver);
    }

    /**
     */
    default void getInvestmentByFarmId(com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetInvestmentByFarmIdMethod(), responseObserver);
    }

    /**
     */
    default void verifyInvestmentFunded(com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifyInvestmentFundedMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service InvestmentService.
   */
  public static abstract class InvestmentServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return InvestmentServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service InvestmentService.
   */
  public static final class InvestmentServiceStub
      extends io.grpc.stub.AbstractAsyncStub<InvestmentServiceStub> {
    private InvestmentServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InvestmentServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InvestmentServiceStub(channel, callOptions);
    }

    /**
     */
    public void getInvestmentById(com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetInvestmentByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getInvestmentByFarmId(com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetInvestmentByFarmIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void verifyInvestmentFunded(com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifyInvestmentFundedMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service InvestmentService.
   */
  public static final class InvestmentServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<InvestmentServiceBlockingStub> {
    private InvestmentServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InvestmentServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InvestmentServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse getInvestmentById(com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetInvestmentByIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse getInvestmentByFarmId(com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetInvestmentByFarmIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse verifyInvestmentFunded(com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifyInvestmentFundedMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service InvestmentService.
   */
  public static final class InvestmentServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<InvestmentServiceFutureStub> {
    private InvestmentServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InvestmentServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InvestmentServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> getInvestmentById(
        com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetInvestmentByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse> getInvestmentByFarmId(
        com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetInvestmentByFarmIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse> verifyInvestmentFunded(
        com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifyInvestmentFundedMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_INVESTMENT_BY_ID = 0;
  private static final int METHODID_GET_INVESTMENT_BY_FARM_ID = 1;
  private static final int METHODID_VERIFY_INVESTMENT_FUNDED = 2;

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
        case METHODID_GET_INVESTMENT_BY_ID:
          serviceImpl.getInvestmentById((com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse>) responseObserver);
          break;
        case METHODID_GET_INVESTMENT_BY_FARM_ID:
          serviceImpl.getInvestmentByFarmId((com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse>) responseObserver);
          break;
        case METHODID_VERIFY_INVESTMENT_FUNDED:
          serviceImpl.verifyInvestmentFunded((com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse>) responseObserver);
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
          getGetInvestmentByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest,
              com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse>(
                service, METHODID_GET_INVESTMENT_BY_ID)))
        .addMethod(
          getGetInvestmentByFarmIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.investmentservice.grpc.InvestmentServiceProto.FarmIdRequest,
              com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentResponse>(
                service, METHODID_GET_INVESTMENT_BY_FARM_ID)))
        .addMethod(
          getVerifyInvestmentFundedMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.investmentservice.grpc.InvestmentServiceProto.InvestmentIdRequest,
              com.agriyield.investmentservice.grpc.InvestmentServiceProto.FundedResponse>(
                service, METHODID_VERIFY_INVESTMENT_FUNDED)))
        .build();
  }

  private static abstract class InvestmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InvestmentServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.investmentservice.grpc.InvestmentServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InvestmentService");
    }
  }

  private static final class InvestmentServiceFileDescriptorSupplier
      extends InvestmentServiceBaseDescriptorSupplier {
    InvestmentServiceFileDescriptorSupplier() {}
  }

  private static final class InvestmentServiceMethodDescriptorSupplier
      extends InvestmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    InvestmentServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (InvestmentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InvestmentServiceFileDescriptorSupplier())
              .addMethod(getGetInvestmentByIdMethod())
              .addMethod(getGetInvestmentByFarmIdMethod())
              .addMethod(getVerifyInvestmentFundedMethod())
              .build();
        }
      }
    }
    return result;
  }
}

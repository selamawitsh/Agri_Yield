package com.agriyield.userservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: user_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserServiceGrpc {

  private UserServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.userservice.grpc.UserService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
      com.agriyield.userservice.grpc.UserServiceProto.UserResponse> getGetUserByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserById",
      requestType = com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest.class,
      responseType = com.agriyield.userservice.grpc.UserServiceProto.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
      com.agriyield.userservice.grpc.UserServiceProto.UserResponse> getGetUserByIdMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest, com.agriyield.userservice.grpc.UserServiceProto.UserResponse> getGetUserByIdMethod;
    if ((getGetUserByIdMethod = UserServiceGrpc.getGetUserByIdMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserByIdMethod = UserServiceGrpc.getGetUserByIdMethod) == null) {
          UserServiceGrpc.getGetUserByIdMethod = getGetUserByIdMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest, com.agriyield.userservice.grpc.UserServiceProto.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetUserById"))
              .build();
        }
      }
    }
    return getGetUserByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
      com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse> getGetFarmerProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetFarmerProfile",
      requestType = com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest.class,
      responseType = com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
      com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse> getGetFarmerProfileMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest, com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse> getGetFarmerProfileMethod;
    if ((getGetFarmerProfileMethod = UserServiceGrpc.getGetFarmerProfileMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetFarmerProfileMethod = UserServiceGrpc.getGetFarmerProfileMethod) == null) {
          UserServiceGrpc.getGetFarmerProfileMethod = getGetFarmerProfileMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest, com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetFarmerProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetFarmerProfile"))
              .build();
        }
      }
    }
    return getGetFarmerProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
      com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse> getGetInvestorProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetInvestorProfile",
      requestType = com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest.class,
      responseType = com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
      com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse> getGetInvestorProfileMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest, com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse> getGetInvestorProfileMethod;
    if ((getGetInvestorProfileMethod = UserServiceGrpc.getGetInvestorProfileMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetInvestorProfileMethod = UserServiceGrpc.getGetInvestorProfileMethod) == null) {
          UserServiceGrpc.getGetInvestorProfileMethod = getGetInvestorProfileMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest, com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetInvestorProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetInvestorProfile"))
              .build();
        }
      }
    }
    return getGetInvestorProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
      com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse> getGetMerchantProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMerchantProfile",
      requestType = com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest.class,
      responseType = com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
      com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse> getGetMerchantProfileMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest, com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse> getGetMerchantProfileMethod;
    if ((getGetMerchantProfileMethod = UserServiceGrpc.getGetMerchantProfileMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetMerchantProfileMethod = UserServiceGrpc.getGetMerchantProfileMethod) == null) {
          UserServiceGrpc.getGetMerchantProfileMethod = getGetMerchantProfileMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest, com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetMerchantProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetMerchantProfile"))
              .build();
        }
      }
    }
    return getGetMerchantProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest,
      com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse> getVerifyUserExistsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "VerifyUserExists",
      requestType = com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest.class,
      responseType = com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest,
      com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse> getVerifyUserExistsMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest, com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse> getVerifyUserExistsMethod;
    if ((getVerifyUserExistsMethod = UserServiceGrpc.getVerifyUserExistsMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getVerifyUserExistsMethod = UserServiceGrpc.getVerifyUserExistsMethod) == null) {
          UserServiceGrpc.getVerifyUserExistsMethod = getVerifyUserExistsMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest, com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "VerifyUserExists"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("VerifyUserExists"))
              .build();
        }
      }
    }
    return getVerifyUserExistsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest,
      com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse> getUpdateAgriScoreMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateAgriScore",
      requestType = com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest.class,
      responseType = com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest,
      com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse> getUpdateAgriScoreMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest, com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse> getUpdateAgriScoreMethod;
    if ((getUpdateAgriScoreMethod = UserServiceGrpc.getUpdateAgriScoreMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getUpdateAgriScoreMethod = UserServiceGrpc.getUpdateAgriScoreMethod) == null) {
          UserServiceGrpc.getUpdateAgriScoreMethod = getUpdateAgriScoreMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest, com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateAgriScore"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("UpdateAgriScore"))
              .build();
        }
      }
    }
    return getUpdateAgriScoreMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest,
      com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse> getGetMerchantIdsByKebeleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMerchantIdsByKebele",
      requestType = com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest.class,
      responseType = com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest,
      com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse> getGetMerchantIdsByKebeleMethod() {
    io.grpc.MethodDescriptor<com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest, com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse> getGetMerchantIdsByKebeleMethod;
    if ((getGetMerchantIdsByKebeleMethod = UserServiceGrpc.getGetMerchantIdsByKebeleMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetMerchantIdsByKebeleMethod = UserServiceGrpc.getGetMerchantIdsByKebeleMethod) == null) {
          UserServiceGrpc.getGetMerchantIdsByKebeleMethod = getGetMerchantIdsByKebeleMethod =
              io.grpc.MethodDescriptor.<com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest, com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetMerchantIdsByKebele"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("GetMerchantIdsByKebele"))
              .build();
        }
      }
    }
    return getGetMerchantIdsByKebeleMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceStub>() {
        @java.lang.Override
        public UserServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceStub(channel, callOptions);
        }
      };
    return UserServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub>() {
        @java.lang.Override
        public UserServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceBlockingStub(channel, callOptions);
        }
      };
    return UserServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub>() {
        @java.lang.Override
        public UserServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceFutureStub(channel, callOptions);
        }
      };
    return UserServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getUserById(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserByIdMethod(), responseObserver);
    }

    /**
     */
    default void getFarmerProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetFarmerProfileMethod(), responseObserver);
    }

    /**
     */
    default void getInvestorProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetInvestorProfileMethod(), responseObserver);
    }

    /**
     */
    default void getMerchantProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMerchantProfileMethod(), responseObserver);
    }

    /**
     */
    default void verifyUserExists(com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getVerifyUserExistsMethod(), responseObserver);
    }

    /**
     */
    default void updateAgriScore(com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateAgriScoreMethod(), responseObserver);
    }

    /**
     */
    default void getMerchantIdsByKebele(com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMerchantIdsByKebeleMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service UserService.
   */
  public static abstract class UserServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return UserServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service UserService.
   */
  public static final class UserServiceStub
      extends io.grpc.stub.AbstractAsyncStub<UserServiceStub> {
    private UserServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceStub(channel, callOptions);
    }

    /**
     */
    public void getUserById(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getFarmerProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetFarmerProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getInvestorProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetInvestorProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMerchantProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMerchantProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void verifyUserExists(com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getVerifyUserExistsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateAgriScore(com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateAgriScoreMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMerchantIdsByKebele(com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMerchantIdsByKebeleMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service UserService.
   */
  public static final class UserServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<UserServiceBlockingStub> {
    private UserServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.userservice.grpc.UserServiceProto.UserResponse getUserById(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserByIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse getFarmerProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetFarmerProfileMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse getInvestorProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetInvestorProfileMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse getMerchantProfile(com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMerchantProfileMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse verifyUserExists(com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getVerifyUserExistsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse updateAgriScore(com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateAgriScoreMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse getMerchantIdsByKebele(com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMerchantIdsByKebeleMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service UserService.
   */
  public static final class UserServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<UserServiceFutureStub> {
    private UserServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.grpc.UserServiceProto.UserResponse> getUserById(
        com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse> getFarmerProfile(
        com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetFarmerProfileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse> getInvestorProfile(
        com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetInvestorProfileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse> getMerchantProfile(
        com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMerchantProfileMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse> verifyUserExists(
        com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getVerifyUserExistsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse> updateAgriScore(
        com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateAgriScoreMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse> getMerchantIdsByKebele(
        com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMerchantIdsByKebeleMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_USER_BY_ID = 0;
  private static final int METHODID_GET_FARMER_PROFILE = 1;
  private static final int METHODID_GET_INVESTOR_PROFILE = 2;
  private static final int METHODID_GET_MERCHANT_PROFILE = 3;
  private static final int METHODID_VERIFY_USER_EXISTS = 4;
  private static final int METHODID_UPDATE_AGRI_SCORE = 5;
  private static final int METHODID_GET_MERCHANT_IDS_BY_KEBELE = 6;

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
        case METHODID_GET_USER_BY_ID:
          serviceImpl.getUserById((com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.UserResponse>) responseObserver);
          break;
        case METHODID_GET_FARMER_PROFILE:
          serviceImpl.getFarmerProfile((com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse>) responseObserver);
          break;
        case METHODID_GET_INVESTOR_PROFILE:
          serviceImpl.getInvestorProfile((com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse>) responseObserver);
          break;
        case METHODID_GET_MERCHANT_PROFILE:
          serviceImpl.getMerchantProfile((com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse>) responseObserver);
          break;
        case METHODID_VERIFY_USER_EXISTS:
          serviceImpl.verifyUserExists((com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse>) responseObserver);
          break;
        case METHODID_UPDATE_AGRI_SCORE:
          serviceImpl.updateAgriScore((com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse>) responseObserver);
          break;
        case METHODID_GET_MERCHANT_IDS_BY_KEBELE:
          serviceImpl.getMerchantIdsByKebele((com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse>) responseObserver);
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
          getGetUserByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
              com.agriyield.userservice.grpc.UserServiceProto.UserResponse>(
                service, METHODID_GET_USER_BY_ID)))
        .addMethod(
          getGetFarmerProfileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
              com.agriyield.userservice.grpc.UserServiceProto.FarmerProfileResponse>(
                service, METHODID_GET_FARMER_PROFILE)))
        .addMethod(
          getGetInvestorProfileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
              com.agriyield.userservice.grpc.UserServiceProto.InvestorProfileResponse>(
                service, METHODID_GET_INVESTOR_PROFILE)))
        .addMethod(
          getGetMerchantProfileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.grpc.UserServiceProto.UserIdRequest,
              com.agriyield.userservice.grpc.UserServiceProto.MerchantProfileResponse>(
                service, METHODID_GET_MERCHANT_PROFILE)))
        .addMethod(
          getVerifyUserExistsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.grpc.UserServiceProto.PhoneRequest,
              com.agriyield.userservice.grpc.UserServiceProto.ExistsResponse>(
                service, METHODID_VERIFY_USER_EXISTS)))
        .addMethod(
          getUpdateAgriScoreMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.grpc.UserServiceProto.UpdateAgriScoreRequest,
              com.agriyield.userservice.grpc.UserServiceProto.EmptyResponse>(
                service, METHODID_UPDATE_AGRI_SCORE)))
        .addMethod(
          getGetMerchantIdsByKebeleMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest,
              com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse>(
                service, METHODID_GET_MERCHANT_IDS_BY_KEBELE)))
        .build();
  }

  private static abstract class UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.userservice.grpc.UserServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserService");
    }
  }

  private static final class UserServiceFileDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier {
    UserServiceFileDescriptorSupplier() {}
  }

  private static final class UserServiceMethodDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    UserServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (UserServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserServiceFileDescriptorSupplier())
              .addMethod(getGetUserByIdMethod())
              .addMethod(getGetFarmerProfileMethod())
              .addMethod(getGetInvestorProfileMethod())
              .addMethod(getGetMerchantProfileMethod())
              .addMethod(getVerifyUserExistsMethod())
              .addMethod(getUpdateAgriScoreMethod())
              .addMethod(getGetMerchantIdsByKebeleMethod())
              .build();
        }
      }
    }
    return result;
  }
}

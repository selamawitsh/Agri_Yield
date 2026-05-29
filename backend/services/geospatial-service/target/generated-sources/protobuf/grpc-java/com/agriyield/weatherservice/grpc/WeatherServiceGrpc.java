package com.agriyield.weatherservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: weather_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class WeatherServiceGrpc {

  private WeatherServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.agriyield.weatherservice.grpc.WeatherService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
      com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse> getGetCurrentWeatherMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetCurrentWeather",
      requestType = com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
      com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse> getGetCurrentWeatherMethod() {
    io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest, com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse> getGetCurrentWeatherMethod;
    if ((getGetCurrentWeatherMethod = WeatherServiceGrpc.getGetCurrentWeatherMethod) == null) {
      synchronized (WeatherServiceGrpc.class) {
        if ((getGetCurrentWeatherMethod = WeatherServiceGrpc.getGetCurrentWeatherMethod) == null) {
          WeatherServiceGrpc.getGetCurrentWeatherMethod = getGetCurrentWeatherMethod =
              io.grpc.MethodDescriptor.<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest, com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetCurrentWeather"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WeatherServiceMethodDescriptorSupplier("GetCurrentWeather"))
              .build();
        }
      }
    }
    return getGetCurrentWeatherMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest,
      com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse> getGetForecastMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetForecast",
      requestType = com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest.class,
      responseType = com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest,
      com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse> getGetForecastMethod() {
    io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest, com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse> getGetForecastMethod;
    if ((getGetForecastMethod = WeatherServiceGrpc.getGetForecastMethod) == null) {
      synchronized (WeatherServiceGrpc.class) {
        if ((getGetForecastMethod = WeatherServiceGrpc.getGetForecastMethod) == null) {
          WeatherServiceGrpc.getGetForecastMethod = getGetForecastMethod =
              io.grpc.MethodDescriptor.<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest, com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetForecast"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WeatherServiceMethodDescriptorSupplier("GetForecast"))
              .build();
        }
      }
    }
    return getGetForecastMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
      com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse> getGetDroughtStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetDroughtStatus",
      requestType = com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
      com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse> getGetDroughtStatusMethod() {
    io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest, com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse> getGetDroughtStatusMethod;
    if ((getGetDroughtStatusMethod = WeatherServiceGrpc.getGetDroughtStatusMethod) == null) {
      synchronized (WeatherServiceGrpc.class) {
        if ((getGetDroughtStatusMethod = WeatherServiceGrpc.getGetDroughtStatusMethod) == null) {
          WeatherServiceGrpc.getGetDroughtStatusMethod = getGetDroughtStatusMethod =
              io.grpc.MethodDescriptor.<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest, com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetDroughtStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WeatherServiceMethodDescriptorSupplier("GetDroughtStatus"))
              .build();
        }
      }
    }
    return getGetDroughtStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
      com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse> getGetWeatherRiskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetWeatherRisk",
      requestType = com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest.class,
      responseType = com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
      com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse> getGetWeatherRiskMethod() {
    io.grpc.MethodDescriptor<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest, com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse> getGetWeatherRiskMethod;
    if ((getGetWeatherRiskMethod = WeatherServiceGrpc.getGetWeatherRiskMethod) == null) {
      synchronized (WeatherServiceGrpc.class) {
        if ((getGetWeatherRiskMethod = WeatherServiceGrpc.getGetWeatherRiskMethod) == null) {
          WeatherServiceGrpc.getGetWeatherRiskMethod = getGetWeatherRiskMethod =
              io.grpc.MethodDescriptor.<com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest, com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetWeatherRisk"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WeatherServiceMethodDescriptorSupplier("GetWeatherRisk"))
              .build();
        }
      }
    }
    return getGetWeatherRiskMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WeatherServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherServiceStub>() {
        @java.lang.Override
        public WeatherServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherServiceStub(channel, callOptions);
        }
      };
    return WeatherServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WeatherServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherServiceBlockingStub>() {
        @java.lang.Override
        public WeatherServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherServiceBlockingStub(channel, callOptions);
        }
      };
    return WeatherServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WeatherServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherServiceFutureStub>() {
        @java.lang.Override
        public WeatherServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherServiceFutureStub(channel, callOptions);
        }
      };
    return WeatherServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getCurrentWeather(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetCurrentWeatherMethod(), responseObserver);
    }

    /**
     */
    default void getForecast(com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetForecastMethod(), responseObserver);
    }

    /**
     */
    default void getDroughtStatus(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetDroughtStatusMethod(), responseObserver);
    }

    /**
     */
    default void getWeatherRisk(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetWeatherRiskMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service WeatherService.
   */
  public static abstract class WeatherServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return WeatherServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service WeatherService.
   */
  public static final class WeatherServiceStub
      extends io.grpc.stub.AbstractAsyncStub<WeatherServiceStub> {
    private WeatherServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherServiceStub(channel, callOptions);
    }

    /**
     */
    public void getCurrentWeather(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetCurrentWeatherMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getForecast(com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetForecastMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getDroughtStatus(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetDroughtStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getWeatherRisk(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request,
        io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetWeatherRiskMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service WeatherService.
   */
  public static final class WeatherServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<WeatherServiceBlockingStub> {
    private WeatherServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse getCurrentWeather(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetCurrentWeatherMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse getForecast(com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetForecastMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse getDroughtStatus(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetDroughtStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse getWeatherRisk(com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetWeatherRiskMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service WeatherService.
   */
  public static final class WeatherServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<WeatherServiceFutureStub> {
    private WeatherServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse> getCurrentWeather(
        com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetCurrentWeatherMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse> getForecast(
        com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetForecastMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse> getDroughtStatus(
        com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetDroughtStatusMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse> getWeatherRisk(
        com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetWeatherRiskMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_CURRENT_WEATHER = 0;
  private static final int METHODID_GET_FORECAST = 1;
  private static final int METHODID_GET_DROUGHT_STATUS = 2;
  private static final int METHODID_GET_WEATHER_RISK = 3;

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
        case METHODID_GET_CURRENT_WEATHER:
          serviceImpl.getCurrentWeather((com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse>) responseObserver);
          break;
        case METHODID_GET_FORECAST:
          serviceImpl.getForecast((com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse>) responseObserver);
          break;
        case METHODID_GET_DROUGHT_STATUS:
          serviceImpl.getDroughtStatus((com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse>) responseObserver);
          break;
        case METHODID_GET_WEATHER_RISK:
          serviceImpl.getWeatherRisk((com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest) request,
              (io.grpc.stub.StreamObserver<com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse>) responseObserver);
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
          getGetCurrentWeatherMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
              com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse>(
                service, METHODID_GET_CURRENT_WEATHER)))
        .addMethod(
          getGetForecastMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest,
              com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse>(
                service, METHODID_GET_FORECAST)))
        .addMethod(
          getGetDroughtStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
              com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse>(
                service, METHODID_GET_DROUGHT_STATUS)))
        .addMethod(
          getGetWeatherRiskMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest,
              com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse>(
                service, METHODID_GET_WEATHER_RISK)))
        .build();
  }

  private static abstract class WeatherServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WeatherServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.agriyield.weatherservice.grpc.WeatherServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("WeatherService");
    }
  }

  private static final class WeatherServiceFileDescriptorSupplier
      extends WeatherServiceBaseDescriptorSupplier {
    WeatherServiceFileDescriptorSupplier() {}
  }

  private static final class WeatherServiceMethodDescriptorSupplier
      extends WeatherServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    WeatherServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (WeatherServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WeatherServiceFileDescriptorSupplier())
              .addMethod(getGetCurrentWeatherMethod())
              .addMethod(getGetForecastMethod())
              .addMethod(getGetDroughtStatusMethod())
              .addMethod(getGetWeatherRiskMethod())
              .build();
        }
      }
    }
    return result;
  }
}

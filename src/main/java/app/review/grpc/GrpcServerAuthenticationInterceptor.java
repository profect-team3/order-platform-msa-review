package app.review.grpc;

import app.commonUtil.security.TokenPrincipalParser;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GrpcServerAuthenticationInterceptor implements ClientInterceptor {

	private final TokenPrincipalParser tokenPrincipalParser;

	private static final Metadata.Key<String> SERVER_AUTH_KEY =
		Metadata.Key.of("Server-Authorization", Metadata.ASCII_STRING_MARSHALLER);

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
		CallOptions callOptions,
		Channel next) {
		// 다음 채널을 호출하여 ClientCall을 가져옵니다
		return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
			next.newCall(method, callOptions)) {

			@Override
			public void start(Listener<RespT> responseListener, Metadata headers) {
				String token = tokenPrincipalParser.tryGetAccessToken().orElse("");

				// 서버 토큰을 헤더에 추가
				headers.put(SERVER_AUTH_KEY, "Server " + token);

				// 다음 ClientCall을 호출
				super.start(responseListener, headers);
			}
		};
	}
}

package app.global.config;

import org.springframework.context.annotation.Configuration;

import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;

import app.commonUtil.security.TokenPrincipalParser;
import app.review.grpc.GrpcServerAuthenticationInterceptor;
import io.grpc.ClientInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class GrpcClientConfig {

	private final TokenPrincipalParser tokenPrincipalParser;

	@GrpcGlobalClientInterceptor
	public ClientInterceptor TokenInterceptor() {
		return new GrpcServerAuthenticationInterceptor(tokenPrincipalParser);
	}

}

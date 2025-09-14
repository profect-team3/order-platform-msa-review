package app.review.grpc;

import org.springframework.stereotype.Component;

import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;

import app.domain.user.grpc.UserInfoServiceGrpc;
import app.domain.user.grpc.UserInfoProto;
import io.grpc.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GrpcUserInfoClient {

	@GrpcClient("userinfo-service")
	private Channel channel;

	public UserInfoProto.GetUserInfoResponse getUserInfo() {
		UserInfoServiceGrpc.UserInfoServiceBlockingStub stub = UserInfoServiceGrpc.newBlockingStub(channel);
		return stub.getUserInfo(Empty.getDefaultInstance());
	}
}

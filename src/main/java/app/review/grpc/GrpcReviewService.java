package app.review.grpc;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import app.commonUtil.apiPayload.ApiResponse;
import app.commonUtil.apiPayload.code.status.ErrorStatus;
import app.commonUtil.apiPayload.exception.GeneralException;
import app.commonUtil.security.TokenPrincipalParser;
import app.domain.user.grpc.UserInfoProto;
import app.review.client.InternalStoreClient;
import app.review.model.ReviewRepository;
import app.review.model.dto.request.CreateReviewRequest;
import app.review.model.entity.Review;
import app.review.status.ReviewErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GrpcReviewService {
	private final ReviewRepository reviewRepository;
	private final InternalStoreClient storeClient;
	private final GrpcUserInfoClient userClient;
	private final TokenPrincipalParser tokenPrincipalParser;

	@Transactional
	public String createReview(Authentication authentication, CreateReviewRequest request) {
		String userIdStr = tokenPrincipalParser.getUserId(authentication);
		Long userId = Long.parseLong(userIdStr);
		if (reviewRepository.existsByOrderId(request.getOrdersId())) {
			throw new GeneralException(ReviewErrorStatus.REVIEW_ALREADY_EXISTS);
		}

		ApiResponse<String> getStoreNameResponse;
		try {
			getStoreNameResponse = storeClient.getStoreName(request.getStoreId());
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			log.error("Store Service Error: {}", e.getResponseBodyAsString());
			throw new GeneralException(ErrorStatus.STORE_NOT_FOUND);
		}

		UserInfoProto.GetUserInfoResponse getUserInfoResponse;
		try {
			getUserInfoResponse = userClient.getUserInfo();
		} catch (Exception e) {
			log.error("User Service Error: {}", e.getMessage());
			throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
		}

		Review review = Review.builder()
			.userId(userId)
			.username(getUserInfoResponse.getUsername())
			.storeId(request.getStoreId())
			.storeName(getStoreNameResponse.result())
			.orderId(request.getOrdersId())
			.rating(request.getRating())
			.content(request.getContent())
			.build();
		Review savedReview = reviewRepository.save(review);
		return "리뷰 : " + savedReview.getReviewId() + " 가 생성되었습니다.";
	}

}
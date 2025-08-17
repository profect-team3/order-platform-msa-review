package app.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.review.client.InternalStoreClient;
import app.review.client.InternalUserClient;
import app.review.model.ReviewRepository;
import app.review.model.dto.request.CreateReviewRequest;
import app.review.model.dto.request.DeleteReviewRequest;
import app.review.model.dto.response.GetReviewResponse;
import app.review.model.dto.response.GetUserInfoResponse;
import app.review.model.entity.Review;
import app.review.status.ReviewErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final InternalStoreClient storeClient;
	private final InternalUserClient userClient;

	@Transactional
	public String createReview(Long userId, CreateReviewRequest request) {
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

		ApiResponse<GetUserInfoResponse> getUserInfoResponse;
		try {
			getUserInfoResponse = userClient.getUserInfo(userId);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("User Service Error: {}", e.getResponseBodyAsString());
			throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
		}

		Review review = Review.builder()
			.userId(userId)
			.username(getUserInfoResponse.result().getUsername())
			.storeId(request.getStoreId())
			.storeName(getStoreNameResponse.result())
			.orderId(request.getOrdersId())
			.rating(request.getRating())
			.content(request.getContent())
			.build();
		Review savedReview = reviewRepository.save(review);
		return "리뷰 : " + savedReview.getReviewId() + " 가 생성되었습니다.";
	}

	public List<GetReviewResponse> getReviews(Long userId) throws GeneralException {
		List<Review> userReviews = reviewRepository.findByUserIdAndDeletedAtIsNull(userId);
		if (userReviews.isEmpty()) {
			throw new GeneralException(ReviewErrorStatus.NO_REVIEWS_FOUND_FOR_USER);
		}
		List<GetReviewResponse> responses = userReviews.stream()
			.map(review -> new GetReviewResponse(
				review.getReviewId(),
				review.getUsername(),
				review.getStoreName(),
				review.getRating(),
				review.getContent(),
				review.getCreatedAt()
			))
			.collect(Collectors.toList());
		return responses;
	}

	@Transactional
	public String deleteReview(Long userId, @Valid DeleteReviewRequest request) {
		Review review = reviewRepository.findById(request.getReviewId())
			.orElseThrow(() -> new GeneralException(ReviewErrorStatus.REVIEW_NOT_FOUND));
		if (!review.getUserId().equals(userId)) {
			throw new GeneralException(ReviewErrorStatus.FORBIDDEN);
		}
		review.delete();
		return "리뷰 : " + request.getReviewId() + " 가 삭제되었습니다.";
	}
}
// app.domain.review.ReviewService.java
package app.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.review.model.ReviewRepository;
import app.review.model.dto.response.GetReviewResponse;
import app.review.model.entity.Review;
import app.review.status.ReviewErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;


	// @Transactional
	// public String createReview(CreateReviewRequest request) {
	// 	User user = securityUtil.getCurrentUser();
	//
	// 	Orders order = ordersRepository.findById(request.getOrdersId())
	// 		.orElseThrow(() -> new GeneralException(ReviewErrorStatus.ORDER_NOT_FOUND));
	//
	// 	if (!order.getUser().equals(user)) {
	// 		throw new GeneralException(ErrorStatus._FORBIDDEN);
	// 	}
	//
	// 	if (reviewRepository.existsByOrders(order)) {
	// 		throw new GeneralException(ReviewErrorStatus.REVIEW_ALREADY_EXISTS);
	// 	}
	//
	// 	Review review = Review.builder()
	// 		.user(user)
	// 		.store(order.getStore())
	// 		.orders(order)
	// 		.rating(request.getRating())
	// 		.content(request.getContent())
	// 		.build();
	//
	// 	Review savedReview = reviewRepository.save(review);
	//
	// 	return "리뷰 : " + savedReview.getReviewId() + " 가 생성되었습니다.";
	// }

	public List<GetReviewResponse> getReviews(Long userId) throws GeneralException {

		List<Review> userReviews = reviewRepository.findByUserId(userId);

		if (userReviews.isEmpty()) {
			throw new GeneralException(ReviewErrorStatus.NO_REVIEWS_FOUND_FOR_USER);
		}

		List<GetReviewResponse> responses = userReviews.stream()
			.map(review -> new GetReviewResponse(
				review.getReviewId(),
				review.getRating(),
				review.getContent(),
				review.getCreatedAt()
			))
			.collect(Collectors.toList());

		return responses;
	}
}
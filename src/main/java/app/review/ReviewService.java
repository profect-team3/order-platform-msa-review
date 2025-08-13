package app.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.review.model.ReviewRepository;
import app.review.model.dto.request.CreateReviewRequest;
import app.review.model.dto.request.DeleteReviewRequest;
import app.review.model.dto.response.GetReviewResponse;
import app.review.model.entity.Review;
import app.review.status.ReviewErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
	private final ReviewRepository reviewRepository;

	@Transactional
	public String createReview(Long userId, CreateReviewRequest request) {
		if (reviewRepository.existsByOrders(request.getOrdersId())) {
			throw new GeneralException(ReviewErrorStatus.REVIEW_ALREADY_EXISTS);
		}
		Review review = Review.builder()
			.userId(userId)
			.store(request.getStoreId())
			.orders(request.getOrdersId())
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
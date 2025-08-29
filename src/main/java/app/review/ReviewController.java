package app.review;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.commonUtil.apiPayload.ApiResponse;
import app.review.model.dto.request.CreateReviewRequest;
import app.review.model.dto.request.DeleteReviewRequest;
import app.review.model.dto.response.GetReviewResponse;
import app.review.status.ReviewSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "review", description = "리뷰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
@PreAuthorize("hasRole('CUSTOMER')")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	@Operation(summary = "리뷰 생성 API", description = "리뷰를 생성합니다.")
	public ApiResponse<String> createReview(
		Authentication authentication,
		@Valid @RequestBody CreateReviewRequest request
	) {
		return ApiResponse.onSuccess(ReviewSuccessStatus.REVIEW_CREATED, reviewService.createReview(authentication, request));
	}

	@GetMapping
	@Operation(summary = "리뷰 조회 API", description = "리뷰를 조회합니다.")
	public ApiResponse<List<GetReviewResponse>> getReviews(
		Authentication authentication
	) {
		return ApiResponse.onSuccess(ReviewSuccessStatus.GET_REVIEWS_SUCCESS, reviewService.getReviews(authentication));
	}
	@PatchMapping
	@Operation(summary = "리뷰 삭제 API", description = "리뷰를 삭제합니다.")
	public ApiResponse<String> deleteReview(
		Authentication authentication,
		@Valid @RequestBody DeleteReviewRequest request
	){
		return ApiResponse.onSuccess((ReviewSuccessStatus.REVIEW_DELETED), reviewService.deleteReview(authentication, request));
	}
}
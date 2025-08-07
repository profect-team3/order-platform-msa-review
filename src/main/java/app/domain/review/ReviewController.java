package app.domain.review;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.domain.review.model.dto.request.CreateReviewRequest;
import app.domain.review.model.dto.response.GetReviewResponse;
import app.domain.review.status.ReviewSuccessStatus;
import app.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "review", description = "리뷰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/review")
public class ReviewController {

	private final ReviewService reviewService;



	@GetMapping
	@Operation(summary = "리뷰 조회 API", description = "리뷰를 조회합니다.")
	public ApiResponse<List<GetReviewResponse>> getReviews(
		@RequestParam Long userId
	) {

		List<GetReviewResponse> a=reviewService.getReviews(userId);
		System.out.println("a = " + a.get(0).toString());
		return ApiResponse.onSuccess(ReviewSuccessStatus.GET_REVIEWS_SUCCESS, a);
	}
}
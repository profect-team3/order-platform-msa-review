package app.review.internal;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import app.global.apiPayload.ApiResponse;
import app.review.model.dto.response.GetReviewResponse;
import app.review.model.dto.response.StoreReviewResponse;
import app.review.status.ReviewSuccessStatus;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InternalStoreController {
	private final InternalStoreService internalStoreService;

	@GetMapping("/Internal/review/average")
	public ApiResponse<List<StoreReviewResponse>> getStoreReviewAverage(@RequestBody List<UUID> storeIds){
		List<StoreReviewResponse> storeReviewResponses=internalStoreService.getStoreReviewAverage(storeIds);
		return ApiResponse.onSuccess(ReviewSuccessStatus.GET_STORE_REVIEW_AVERAGE_SUCCESS,storeReviewResponses);
	}

	@GetMapping("/Internal/review/{storeId}")
	public ApiResponse<List<GetReviewResponse>> getReviewsByStoreId(@PathVariable UUID storeId){
		List<GetReviewResponse> reviewResponse = internalStoreService.getReviewsByStoreId(storeId);
		return ApiResponse.onSuccess(ReviewSuccessStatus.GET_STORE_REVIEW_AVERAGE_SUCCESS,reviewResponse);
	}

}

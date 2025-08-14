package app.review.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.review.model.ReviewRepository;
import app.review.model.dto.response.GetReviewResponse;
import app.review.model.dto.response.StoreReviewResponse;
import app.review.model.entity.Review;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InternalStoreService {
	private final ReviewRepository reviewRepository;
	
	public List<StoreReviewResponse> getStoreReviewAverage(List<UUID> storeIds){
		List<StoreReviewResponse> response=new LinkedList<>();
		storeIds.stream().forEach(storeId->{
			try {
				List<Review> reviews=reviewRepository.findByStoreId(storeId);
				Double average=reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
				StoreReviewResponse storeReviewResponse=new StoreReviewResponse(storeId,Long.valueOf(reviews.size()),average);
				response.add(storeReviewResponse);
			} catch (Exception e) {
				throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
			}
		});
		return response;
	}

	public List<GetReviewResponse> getReviewsByStoreId(UUID storeId){
		try {
			List<Review> reviews=reviewRepository.findByStoreId(storeId);
			List<GetReviewResponse> response=reviews.stream().map(review->new GetReviewResponse(
				review.getReviewId(),
				review.getUsername(),
				review.getStoreName(),
				review.getRating(),
				review.getContent(),
				review.getCreatedAt()
			)).toList();
			return response;
		} catch (Exception e) {
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}
}
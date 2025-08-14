package app.review.model.dto.response;

import java.util.UUID;

import lombok.Getter;

@Getter
public class StoreReviewResponse {
	private UUID storeId;
	private Long number;
	private Double average;

	public StoreReviewResponse(){
	}
	public StoreReviewResponse(UUID storeId, Long number, Double average){
		this.storeId=storeId;
		this.number=number;
		this.average=average;
	}
}

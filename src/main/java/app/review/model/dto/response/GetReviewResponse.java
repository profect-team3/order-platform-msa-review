package app.review.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GetReviewResponse {
	private UUID reviewId;
	private String username;
	private String storeName;
	private Long rating;
	private String content;
	private LocalDateTime createdAt;

	public GetReviewResponse() {
	}

	public GetReviewResponse(UUID reviewId, String username, String storeName, Long rating, String content, LocalDateTime createdAt) {
		this.reviewId = reviewId;
		this.username=username;
		this.storeName=storeName;
		this.rating = rating;
		this.content = content;
		this.createdAt = createdAt;
	}

	public UUID getReviewId() {
		return reviewId;
	}

	public void setReviewId(UUID reviewId) {
		this.reviewId = reviewId;
	}


	public Long getRating() {
		return rating;
	}

	public void setRating(Long rating) {
		this.rating = rating;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
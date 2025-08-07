package app.review.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.ToString;

@ToString
public class GetReviewResponse {
	@Schema(description = "리뷰 ID", example = "123e4567-e89b-12d3-a456-426614174001")
	private UUID reviewId;
	@Schema(description = "평점", example = "5")
	private Long rating;
	@Schema(description = "리뷰 내용", example = "맛있어요!")
	private String content;
	@Schema(description = "작성일", example = "2024-07-31T12:00:00")
	private LocalDateTime createdAt;

	public GetReviewResponse() {
	}

	public GetReviewResponse(UUID reviewId, Long rating, String content, LocalDateTime createdAt) {
		this.reviewId = reviewId;
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
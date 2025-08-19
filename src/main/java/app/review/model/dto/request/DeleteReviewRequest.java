package app.review.model.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteReviewRequest {
	private UUID reviewId;
}

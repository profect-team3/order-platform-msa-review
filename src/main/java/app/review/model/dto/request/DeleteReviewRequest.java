package app.review.model.dto.request;

import java.util.UUID;
import lombok.Getter;

@Getter
public class DeleteReviewRequest {
	private UUID reviewId;
}

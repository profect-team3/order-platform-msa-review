package app.review.model;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.review.model.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

	List<Review> findByUserIdAndDeletedAtIsNull(Long userid);

	List<Review> findByStoreId(UUID storeId);

	boolean existsByOrderId(UUID orderId);
}

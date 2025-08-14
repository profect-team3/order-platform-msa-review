package app.review.model;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.review.model.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

	List<Review> findByUserIdAndDeletedAtIsNull(Long userid);

	// @Query("SELECT AVG(r.rating) FROM Review r WHERE r.store.storeId = :storeId")
	// Double getAverageRatingByStore(@Param("storeId") UUID storeId);
	//
	// List<Review> findByStore(Store store);
	//

	List<Review> findByStoreId(UUID storeId);
	boolean existsByOrders(UUID ordersId);
}

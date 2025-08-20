package app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.HttpClientErrorException;

import app.commonSecurity.TokenPrincipalParser;
import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.code.status.SuccessStatus;
import app.global.apiPayload.exception.GeneralException;
import app.review.ReviewService;
import app.review.client.InternalStoreClient;
import app.review.client.InternalUserClient;
import app.review.model.ReviewRepository;
import app.review.model.dto.request.CreateReviewRequest;
import app.review.model.dto.request.DeleteReviewRequest;
import app.review.model.dto.response.GetReviewResponse;
import app.review.model.dto.response.GetUserInfoResponse;
import app.review.model.entity.Review;
import app.review.status.ReviewErrorStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService Test")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private InternalStoreClient storeClient;

    @Mock
    private InternalUserClient userClient;

    @Mock
    private TokenPrincipalParser tokenPrincipalParser;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private Authentication authentication;

    private Long userId = 1L;
    private Long otherUserId = 2L;
    private UUID storeId = UUID.randomUUID();
    private UUID orderId = UUID.randomUUID();
    private UUID reviewId = UUID.randomUUID();
	private String username= "test";
	private String email="example@naver.com";
	private String nickName= "test";
	private String realName= "test";
	private String phoneNumber= "010-1234-5678";
	private String userRole= "CUSTOMER";


    private Review review;

    @BeforeEach
    void setUp() {
        review = Review.builder()
                .reviewId(reviewId)
                .userId(userId)
                .username("testuser")
                .storeId(storeId)
                .storeName("teststore")
                .orderId(orderId)
                .rating(5L)
                .content("Great!")
                .build();
    }

    @Test
    @DisplayName("리뷰 생성 - 성공")
    void createReview_Success() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(storeId, orderId, 5L, "Great!");
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(userId));
        when(reviewRepository.existsByOrderId(request.getOrdersId())).thenReturn(false);

        ApiResponse<String> storeResponse = ApiResponse.onSuccess(SuccessStatus._OK,"성공");
        when(storeClient.getStoreName(request.getStoreId())).thenReturn(storeResponse);

        GetUserInfoResponse userInfo = new GetUserInfoResponse(userId,username,email,nickName,realName,phoneNumber,userRole);
        ApiResponse<GetUserInfoResponse> userResponse = ApiResponse.onSuccess(SuccessStatus._OK,userInfo);
        when(userClient.getUserInfo()).thenReturn(userResponse);

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review savedReview = invocation.getArgument(0);
            return Review.builder()
                    .reviewId(reviewId)
                    .userId(savedReview.getUserId())
                    .username(savedReview.getUsername())
                    .storeId(savedReview.getStoreId())
                    .storeName(savedReview.getStoreName())
                    .orderId(savedReview.getOrderId())
                    .rating(savedReview.getRating())
                    .content(savedReview.getContent())
                    .build();
        });

        // when
        String result = reviewService.createReview(authentication, request);

        // then
        assertNotNull(result);
        assertTrue(result.contains("리뷰 : " + reviewId + " 가 생성되었습니다."));
        verify(reviewRepository).existsByOrderId(request.getOrdersId());
        verify(storeClient, times(1)).getStoreName(request.getStoreId());
        verify(userClient, times(1)).getUserInfo();
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 생성 - 실패 (이미 리뷰 존재)")
    void createReview_Fail_ReviewAlreadyExists() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(storeId, orderId, 5L, "Great!");
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(userId));
        when(reviewRepository.existsByOrderId(request.getOrdersId())).thenReturn(true);

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> reviewService.createReview(authentication, request));
        assertEquals(ReviewErrorStatus.REVIEW_ALREADY_EXISTS, exception.getCode());
    }

    @Test
    @DisplayName("리뷰 생성 - 실패 (Store-Service 통신 오류)")
    void createReview_Fail_StoreServiceError() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(storeId, orderId, 5L, "Great!");
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(userId));
        when(reviewRepository.existsByOrderId(request.getOrdersId())).thenReturn(false);
        when(storeClient.getStoreName(request.getStoreId())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Store Server Error", new byte[0], null));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> reviewService.createReview(authentication, request));
        assertEquals(ErrorStatus.STORE_NOT_FOUND, exception.getCode());
    }

    @Test
    @DisplayName("리뷰 생성 - 실패 (User-Service 통신 오류)")
    void createReview_Fail_UserServiceError() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(storeId, orderId, 5L, "Great!");
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(userId));
        when(reviewRepository.existsByOrderId(request.getOrdersId())).thenReturn(false);

        ApiResponse<String> storeResponse = ApiResponse.onSuccess(SuccessStatus._OK,"성공");
        when(storeClient.getStoreName(request.getStoreId())).thenReturn(storeResponse);
        when(userClient.getUserInfo()).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Store Server Error", new byte[0], null));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> reviewService.createReview(authentication, request));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getCode());
    }

    @Test
    @DisplayName("사용자 리뷰 조회 - 성공")
    void getReviews_Success() {
        // given
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(userId));
        when(reviewRepository.findByUserIdAndDeletedAtIsNull(userId)).thenReturn(Collections.singletonList(review));

        // when
        List<GetReviewResponse> responses = reviewService.getReviews(authentication);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        GetReviewResponse response = responses.get(0);
        assertEquals(review.getReviewId(), response.getReviewId());
        assertEquals(review.getUsername(), response.getUsername());
        assertEquals(review.getStoreName(), response.getStoreName());
        assertEquals(review.getRating(), response.getRating());
        assertEquals(review.getContent(), response.getContent());
        verify(reviewRepository, times(1)).findByUserIdAndDeletedAtIsNull(userId);
    }

    @Test
    @DisplayName("사용자 리뷰 조회 - 실패 (리뷰 없음)")
    void getReviews_Fail_NoReviewsFound() {
        // given
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(userId));
        when(reviewRepository.findByUserIdAndDeletedAtIsNull(userId)).thenReturn(Collections.emptyList());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> reviewService.getReviews(authentication));
        assertEquals(ReviewErrorStatus.NO_REVIEWS_FOUND_FOR_USER, exception.getCode());
    }

    @Test
    @DisplayName("리뷰 삭제 - 성공")
    void deleteReview_Success() {
        // given
        DeleteReviewRequest request = new DeleteReviewRequest(reviewId);
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(userId));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));

        // when
        String result = reviewService.deleteReview(authentication, request);

        // then
        assertNotNull(result);
        assertEquals("리뷰 : " + reviewId + " 가 삭제되었습니다.", result);
        assertNotNull(review.getDeletedAt()); // Check if deletedAt is set
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("리뷰 삭제 - 실패 (리뷰 없음)")
    void deleteReview_Fail_ReviewNotFound() {
        // given
        DeleteReviewRequest request = new DeleteReviewRequest(reviewId);
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(userId));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> reviewService.deleteReview(authentication, request));
        assertEquals(ReviewErrorStatus.REVIEW_NOT_FOUND, exception.getCode());
    }

    @Test
    @DisplayName("리뷰 삭제 - 실패 (권한 없음)")
    void deleteReview_Fail_Forbidden() {
        // given
        DeleteReviewRequest request = new DeleteReviewRequest(reviewId);
        when(tokenPrincipalParser.getUserId(authentication)).thenReturn(String.valueOf(otherUserId)); // Different user
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> reviewService.deleteReview(authentication, request));
        assertEquals(ReviewErrorStatus.FORBIDDEN, exception.getCode());
    }
}

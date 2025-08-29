package app.controller;

import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.commonUtil.apiPayload.code.status.ErrorStatus;
import app.commonUtil.apiPayload.exception.GeneralException;
import app.review.ReviewController;
import app.review.ReviewService;
import app.review.model.dto.request.CreateReviewRequest;
import app.review.model.dto.response.GetReviewResponse;
import app.review.status.ReviewErrorStatus;
import app.review.status.ReviewSuccessStatus;

@WebMvcTest(ReviewController.class)
@DisplayName("ReviewController 테스트")
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockitoBean
	private ReviewService reviewService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.build();
	}

	@Test
	@DisplayName("리뷰 생성 - 성공")
	void createReview_Success() throws Exception {
		CreateReviewRequest request = new CreateReviewRequest(UUID.randomUUID(),UUID.randomUUID(), 5L, "맛있어요");
		String resultMessage = "리뷰 : " + UUID.randomUUID() + " 가 생성되었습니다.";

		when(reviewService.createReview(any(),any(CreateReviewRequest.class)))
			.thenReturn(resultMessage);

		mockMvc.perform(post("")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(ReviewSuccessStatus.REVIEW_CREATED.getCode()))
			.andExpect(jsonPath("$.message").value(ReviewSuccessStatus.REVIEW_CREATED.getMessage()))

			.andExpect(jsonPath("$.result").value(resultMessage));
	}

	@Test
	@DisplayName("리뷰 조회 - 성공")
	void getReviews_Success() throws Exception {
		GetReviewResponse reviewResponse = new GetReviewResponse(
			UUID.randomUUID(),
			"testuser",
			"teststore",
			5L,
			"Great!",
			LocalDateTime.now()
		);
		List<GetReviewResponse> responseList = Collections.singletonList(reviewResponse);

		when(reviewService.getReviews(any()))
			.thenReturn(responseList);

		mockMvc.perform(get("")).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(ReviewSuccessStatus.GET_REVIEWS_SUCCESS.getCode()))
			.andExpect(jsonPath("$.message").value(ReviewSuccessStatus.GET_REVIEWS_SUCCESS.getMessage()))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result.length()").value(1))
			.andExpect(jsonPath("$.result[0].username").value(reviewResponse.getUsername()))
			.andExpect(jsonPath("$.result[0].storeName").value(reviewResponse.getStoreName()))
			.andExpect(jsonPath("$.result[0].rating").value(reviewResponse.getRating()))
			.andExpect(jsonPath("$.result[0].content").value(reviewResponse.getContent()));
	}

	@Test
	@DisplayName("리뷰 생성 - 실패 (서비스 예외)")
	void createReview_Fail_ServiceException() throws Exception {
		CreateReviewRequest request = new CreateReviewRequest(UUID.randomUUID(),UUID.randomUUID(), 5L, "맛있어요");
		when(reviewService.createReview(any(),any(CreateReviewRequest.class)))
			.thenThrow(new GeneralException(ReviewErrorStatus.REVIEW_ALREADY_EXISTS));

		mockMvc.perform(post("")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value(ReviewErrorStatus.REVIEW_ALREADY_EXISTS.getCode()))
			.andExpect(jsonPath("$.message").value(ReviewErrorStatus.REVIEW_ALREADY_EXISTS.getMessage()));
	}

	@Test
	@DisplayName("리뷰 조회 - 실패 (사용자 없음)")
	void getReviews_Fail_UserNotFound() throws Exception {
		when(reviewService.getReviews(any()))
			.thenThrow(new GeneralException(ErrorStatus.USER_NOT_FOUND));

		mockMvc.perform(get(""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value(ErrorStatus.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorStatus.USER_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("리뷰 조회 - 실패 (리뷰 없음)")
	void getReviews_Fail_NoReviewsFound() throws Exception {
		when(reviewService.getReviews(any()))
			.thenThrow(new GeneralException(ReviewErrorStatus.NO_REVIEWS_FOUND_FOR_USER));

		mockMvc.perform(get(""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value(ReviewErrorStatus.NO_REVIEWS_FOUND_FOR_USER.getCode()))
			.andExpect(jsonPath("$.message").value(ReviewErrorStatus.NO_REVIEWS_FOUND_FOR_USER.getMessage()));
	}

	@Test
	@DisplayName("리뷰 생성 - 실패 (입력 검증 실패)")
	void createReview_Fail_InvalidInput() throws Exception {
		CreateReviewRequest request = new CreateReviewRequest(null,null, null, "");

		mockMvc.perform(post("")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("COMMON400"));

	}
}
package newbie.place_review.module.review.impl;

import newbie.place_review.module.review.ReviewImage;
import newbie.place_review.module.review.ReviewImageModuleImpl;
import newbie.place_review.module.review.ReviewImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewImageModuleImpl 테스트")
class ReviewImageModuleImplTest {

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @InjectMocks
    private ReviewImageModuleImpl reviewImageModule;

    @Test
    @DisplayName("Id로 리뷰이미지 찾기")
    void Try_to_find_ReviewImage_By_Id() {
        // Given
        when(reviewImageRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mock(ReviewImage.class)));

        // When
        Optional<ReviewImage> optReviewImage = reviewImageModule.findById(1L);

        // Then
        assertTrue(optReviewImage.isPresent());
    }

    @Test
    @DisplayName("리뷰이미지를 제거하려 했으나 찾지 못함")
    void deleteById() {
        // Given
        when(reviewImageRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        // Then
        assertThrows(DataRetrievalFailureException.class, () -> {
            reviewImageModule.deleteById(1L);
        });
    }
}

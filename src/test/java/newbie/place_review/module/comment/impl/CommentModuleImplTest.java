package newbie.place_review.module.comment.impl;

import newbie.place_review.module.comment.CommentModuleImpl;
import newbie.place_review.module.comment.CommentRepository;
import newbie.place_review.module.comment.Comments;
import newbie.place_review.module.review.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentModuleImplTest 테스트")
class CommentModuleImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentModuleImpl commentModule;

    @Test
    @DisplayName("비회원이 댓글을 저장할 때")
    void Nonmember_saves_a_comment() {
        //Given
        Review review = mock(Review.class);
        Comments comments = mock(Comments.class);

        when(commentRepository.save(any(Comments.class))).thenReturn(comments);

        //When
        //Then
        assertEquals(comments, commentModule.saveByNonmember(review, "내용", "테스트 비밀번호"));
    }
}

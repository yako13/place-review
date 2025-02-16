package newbie.place_review.module.comment.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import newbie.place_review.module.comment.CommentModule;
import newbie.place_review.module.comment.CommentRepository;
import newbie.place_review.module.comment.Comments;
import newbie.place_review.module.review.Review;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CommentModuleImpl implements CommentModule {

    private final CommentRepository commentRepository;

    @Override
    public Comments saveByNonmember(Review review, String content, String password) {
        Assert.notNull(review, "Review cannot be null");
        Assert.notNull(content, "Content cannot be null");
        Assert.notNull(password, "Password cannot be null");

        Comments comment = Comments.builder()
                                   .review(review)
                                   .content(content)
                                   .password(password)
                                   .build();

        return commentRepository.save(comment);
    }


    @Override
    public Optional<Comments> getById(Long commentId) {
        Assert.notNull(commentId, "CommentId cannot be null");

        return commentRepository.findById(commentId);
    }

    @Override
    public void deleteByNonmember(Long commentId, String password) {
        Assert.notNull(commentId, "CommentId cannot be null");
        Assert.notNull(password, "Password cannot be null");

        getById(commentId).ifPresentOrElse(
                comments -> {
                    if (comments.getPassword().equals(password))
                        commentRepository.delete(comments);
                    else
                        throw new SecurityException("댓글의 비밀번호가 일치하지 않습니다.");
                },
                () -> {
                    throw new DataRetrievalFailureException("삭제할 댓글을 찾을 수 없습니다.");
                }
        );
    }
}

package newbie.place_review.module.review.impl;

import lombok.RequiredArgsConstructor;
import newbie.place_review.module.place.Place;
import newbie.place_review.module.review.Review;
import newbie.place_review.module.review.ReviewImage;
import newbie.place_review.module.review.ReviewImageModule;
import newbie.place_review.module.review.ReviewImageRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewImageModuleImpl implements ReviewImageModule {

    private final ReviewImageRepository reviewImageRepository;

    @Override
    public Optional<ReviewImage> findById(Long reviewImageId) {
        Assert.notNull(reviewImageId, "ReviewImageId cannot be null");

        return reviewImageRepository.findById(reviewImageId);
    }

    @Override
    public ReviewImage save(String name, Place place, Review review) {
        Assert.notNull(name, "Name cannot be null");
        Assert.notNull(place, "Place cannot be null");
        Assert.notNull(review, "Review cannot be null");

        ReviewImage reviewImage = ReviewImage.builder()
                                             .name(name)
                                             .place(place)
                                             .review(review)
                                             .build();

        return reviewImageRepository.save(reviewImage);
    }

    @Override
    public void deleteById(Long reviewImageId) throws DataRetrievalFailureException {
        Assert.notNull(reviewImageId, "ReviewImageId cannot be null");

        findById(reviewImageId).ifPresentOrElse(
                reviewImageRepository::delete,
                () -> {
                    throw new DataRetrievalFailureException("삭제하려는 리뷰를 찾을 수 없습니다.");
                });
    }
}

package newbie.place_review.module.visit.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import newbie.place_review.module.place.Place;
import newbie.place_review.module.visit.Visit;
import newbie.place_review.module.visit.VisitModule;
import newbie.place_review.module.visit.VisitRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VisitModuleImpl implements VisitModule {

    private final VisitRepository visitRepository;

    @Override
    public Visit save(Place place, Long visitCount, LocalDate date) {

        Assert.notNull(place, "Place cannot be null");
        Assert.notNull(visitCount, "VisitCount cannot be null");
        Assert.notNull(date, "Date cannot be null");

        Visit visit = Visit.builder()
                           .date(date)
                           .visitCount(visitCount)
                           .place(place)
                           .build();

        return visitRepository.save(visit);
    }

    @Override
    public Optional<Visit> getById(Long visitId) {
        Assert.notNull(visitId, "VisitId cannot be null");

        return visitRepository.findById(visitId);
    }

    @Override
    public Visit update(Long visitId, Long visitCount) {
        Assert.notNull(visitId, "VisitId cannot be null");
        Assert.notNull(visitCount, "VisitCount cannot be null");

        return getById(visitId).map((visit) -> {
            visit.setVisitCount(visitCount);
            return visit;
        }).orElseThrow(() -> new DataRetrievalFailureException("찾는 방문이 없습니다."));
    }
}

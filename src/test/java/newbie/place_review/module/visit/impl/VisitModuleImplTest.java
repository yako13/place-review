package newbie.place_review.module.visit.impl;

import newbie.place_review.module.place.Place;
import newbie.place_review.module.visit.Visit;
import newbie.place_review.module.visit.VisitModuleImpl;
import newbie.place_review.module.visit.VisitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("VisitModuleImplTest 테스트")
class VisitModuleImplTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitModuleImpl visitModule;

    @Test
    @DisplayName("방문 저장")
    void try_to_save_a_visit() {
        //Given
        Visit visit = mock(Visit.class);
        Place place = mock(Place.class);
        when(visitRepository.save(any(Visit.class))).thenReturn(visit);

        //When
        //Then
        assertEquals(visit, visitModule.save(place, 1L, LocalDate.now()));

    }

    @Test
    @DisplayName("잘못된 방문 아이디로 방문 조회")
    void find_by_wrong_visit_id() {
        //Given
        when(visitRepository.findById(2L)).thenReturn(Optional.empty());

        //When
        Optional<Visit> optVisit = visitModule.getById(2L);

        //Then
        assertNull(optVisit.orElse(null));
    }

    @Test
    @DisplayName("정확한 방문 아이디로 방문 조회")
    void find_by_correct_visit_id() {
        //Given
        Visit visit = mock(Visit.class);
        when(visitRepository.findById(1L)).thenReturn(Optional.ofNullable(visit));

        //When
        //Then
        assertEquals(visit, visitModule.getById(1L).orElse(null));
    }

    @Test
    @DisplayName("잘못된 방문 아이디로 업데이트 시도")
    void try_to_update_with_wrong_visit_id() {
        //Given
        when(visitRepository.findById(2L)).thenReturn(Optional.empty());

        //When
        //Then
        assertThrows(DataRetrievalFailureException.class, () -> visitModule.update(2L, 4L));
    }

    @Test
    @DisplayName("정확한 방문 아이디로 업데이트 시도")
    void try_to_update_with_correct_visit_id() {
        //Given
        Visit visit = Visit.builder()
                           .place(mock(Place.class))
                           .visitCount(1L)
                           .date(LocalDate.now())
                           .build();


        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        //When
        //Then
        assertEquals(2L,visitModule.update(1L,2L).getVisitCount());
    }
}

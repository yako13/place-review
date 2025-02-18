package newbie.place_review.module.place.impl;

import newbie.place_review.module.place.Coordinates;
import newbie.place_review.module.place.Place;
import newbie.place_review.module.place.PlaceModuleImpl;
import newbie.place_review.module.place.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaceModuleImplTest 테스트")
class PlaceModuleImplTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceModuleImpl placeModule;


    @Test
    @DisplayName("장소 아이디로 장소를 찾을 때")
    void Find_a_place_by_placeId() {
        // Given
        when(placeRepository.findById(1L)).thenReturn(Optional.ofNullable(mock(Place.class)));

        // When
        Optional<Place> optPlace = placeModule.getById(1L);

        // Then
        assertNotNull(optPlace.orElse(null));
    }

    @Test
    @DisplayName("장소를 수정할 때")
    void update() {
        // Given
        Place place = Place.builder()
                           .address("임의의 주소")
                           .placeName("임의의 장소이름")
                           .coordinates(mock(Coordinates.class))
                           .build();

        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

        // When
        Place changedPlace = placeModule.update(1L, "수정된 주소", "수정된 장소명", 100D, 101D);

        // Then
        assertEquals("수정된 주소", changedPlace.getAddress());
        assertEquals("수정된 장소명", changedPlace.getPlaceName());
    }

    @Test
    @DisplayName("존재하지 않는 장소를 수정할 때")
    void Try_to_change_a_place_not_exist() {
        // Given
        when(placeRepository.findById(404L)).thenReturn(Optional.empty());

        // When
        // Then
        assertThrows(DataRetrievalFailureException.class, () -> placeModule.update(404L, "임의의 주소", "임의의 장소명", 102.2, 103.2));
    }
}

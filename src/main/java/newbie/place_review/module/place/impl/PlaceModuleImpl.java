package newbie.place_review.module.place.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import newbie.place_review.module.place.Coordinates;
import newbie.place_review.module.place.Place;
import newbie.place_review.module.place.PlaceModule;
import newbie.place_review.module.place.PlaceRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceModuleImpl implements PlaceModule {

    private final PlaceRepository placeRepository;

    @Override
    public Place save(
            String address,
            String placeName,
            Double latitude,
            Double longitude
    ) {
        Assert.notNull(address, "Address cannot be null");
        Assert.notNull(placeName, "PlaceName cannot be null");
        Assert.notNull(latitude, "Latitude cannot be null");
        Assert.notNull(longitude, "Longitude cannot be null");

        Coordinates coordinates = Coordinates.builder()
                                             .longitude(longitude)
                                             .latitude(latitude)
                                             .build();

        Place place = Place.builder()
                           .coordinates(coordinates)
                           .placeName(placeName)
                           .address(address)
                           .build();

        return placeRepository.save(place);
    }


    @Override
    public Optional<Place> getById(Long placeId) {
        Assert.notNull(placeId, "PlaceId cannot be null");

        return placeRepository.findById(placeId);
    }

    @Override
    public void deleteById(Long placeId) {
        Assert.notNull(placeId, "PlaceId cannot be null");

        placeRepository.findById(placeId).ifPresentOrElse(
                placeRepository::delete,
                () -> {
                    throw new DataRetrievalFailureException("삭제 할 장소를 찾지 못하였습니다.");
                }
        );
    }

    @Override
    public Place update(
            Long placeId,
            String address,
            String placeName,
            Double latitude,
            Double longitude
    ) {
        Assert.notNull(placeId, "PlaceId cannot be null");
        Assert.notNull(address, "Address cannot be null");
        Assert.notNull(placeId, "PlaceName cannot be null");
        Assert.notNull(latitude, "Latitude cannot be null");
        Assert.notNull(longitude, "Longitude cannot be null");

        Place foundPlace = placeRepository.findById(placeId).map(place -> {

            Coordinates coordinates = Coordinates.builder()
                                                 .latitude(latitude)
                                                 .longitude(longitude)
                                                 .build();

            place.setAddress(address);
            place.setPlaceName(placeName);
            place.setCoordinates(coordinates);


            return place;
        }).orElseThrow(() -> new DataRetrievalFailureException("수정 할 장소를 찾지 못하였습니다."));

        return foundPlace;
    }
}

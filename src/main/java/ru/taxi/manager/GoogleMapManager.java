package ru.taxi.manager;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import org.springframework.stereotype.Component;
import ru.taxi.model.RouteInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleMapManager {
    private final GeoApiContext context;

    public GoogleMapManager() {
        context = new GeoApiContext.Builder()
                .apiKey("YOUR_API_KEY")
                .build();
    }

    public List<RouteInfo> query(String from, String to) {
        try {
            final DirectionsResult result = DirectionsApi
                    .getDirections(context, from, to)
                    .await();
            final List<RouteInfo> infos = new ArrayList<>(result.routes.length);
            for (DirectionsRoute route : result.routes) {
                long distance = 0;
                long duration = 0;
                for (DirectionsLeg leg : route.legs) {
                    distance += leg.distance.inMeters;
                    duration += leg.duration.inSeconds;
                }
                final RouteInfo info = new RouteInfo(distance, duration);
                infos.add(info);
            }
            return infos;
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

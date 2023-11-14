package cz.cvut.fit.marzondr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
class TourServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourServiceApplication.class, args);
    }
}

@RestController
@EnableAutoConfiguration
class TourController {

    private Map<Integer, Tour> tours = new HashMap<>();
    private Map<Integer, TourRequest> tourRequests = new HashMap<>();

    @DeleteMapping("/tours/{id}")
    public ResponseEntity<TourRequest> requestTourDeletion(@PathVariable int id) {
        if (tours.containsKey(id)) {
            Instant requestTime = Instant.now();
            tourRequests.put(id, new TourRequest(id, requestTime));
            return ResponseEntity.ok(new TourRequest(id, requestTime));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/tours/{id}/confirm")
    public ResponseEntity<TourRequest> confirmTourDeletion(@PathVariable int id) {
        if (tours.containsKey(id) && tourRequests.containsKey(id)) {
            tours.remove(id);
            TourRequest request = tourRequests.remove(id);
            request.setDeletionTime(Instant.now());
            return ResponseEntity.ok(request);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tours")
    public ResponseEntity<List<TourStatus>> getTourStatus() {
        List<TourStatus> tourStatusList = new ArrayList<>();
        for (int id : tours.keySet()) {
            boolean isDeleted = tourRequests.containsKey(id) && tourRequests.get(id).getDeletionTime() != null;
            boolean isRequestedForDeletion = tourRequests.containsKey(id);
            tourStatusList.add(new TourStatus(id, isDeleted, isRequestedForDeletion));
        }
        return ResponseEntity.ok(tourStatusList);
    }

    @PostMapping("/tours")
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        tours.put(tour.getId(), tour);
        return ResponseEntity.ok(tour);
    }
}

class Tour {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class TourRequest {
    private int tourId;
    private Instant requestTime;
    private Instant deletionTime;

    public TourRequest(int tourId, Instant requestTime) {
        this.tourId = tourId;
        this.requestTime = requestTime;
    }

    public int getTourId() {
        return tourId;
    }

    public Instant getRequestTime() {
        return requestTime;
    }

    public Instant getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(Instant deletionTime) {
        this.deletionTime = deletionTime;
    }
}

class TourStatus {
    private int tourId;
    private boolean isDeleted;
    private boolean isRequestedForDeletion;

    public TourStatus(int tourId, boolean isDeleted, boolean isRequestedForDeletion) {
        this.tourId = tourId;
        this.isDeleted = isDeleted;
        this.isRequestedForDeletion = isRequestedForDeletion;
    }

    public int getTourId() {
        return tourId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isRequestedForDeletion() {
        return isRequestedForDeletion;
    }
}

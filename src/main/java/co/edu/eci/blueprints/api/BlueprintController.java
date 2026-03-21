package co.edu.eci.blueprints.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/blueprints")
public class BlueprintController {

    public record Point(int x, int y) {}

    public record Blueprint(
            String id,
            String author,
            String name,
            List<Point> points
    ) {}

    public record BlueprintRequest(
            String author,
            String name,
            List<Point> points
    ) {}

    private final Map<String, Blueprint> storage = new ConcurrentHashMap<>();

    public BlueprintController() {
        storage.put(
                "b1",
                new Blueprint(
                        "b1",
                        "student",
                        "Casa de campo",
                        List.of(
                                new Point(30, 220),
                                new Point(30, 120),
                                new Point(120, 120),
                                new Point(120, 220),
                                new Point(30, 220)
                        )
                )
        );

        storage.put(
                "b2",
                new Blueprint(
                        "b2",
                        "student",
                        "Edificio urbano",
                        List.of(
                                new Point(180, 240),
                                new Point(180, 80),
                                new Point(260, 80),
                                new Point(260, 240)
                        )
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public List<Blueprint> list() {
        return new ArrayList<>(storage.values());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public ResponseEntity<Blueprint> get(@PathVariable String id) {
        Blueprint blueprint = storage.get(id);
        if (blueprint == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blueprint);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public ResponseEntity<Blueprint> create(@RequestBody BlueprintRequest in) {
        String id = "b" + (storage.size() + 1);

        Blueprint blueprint = new Blueprint(
                id,
                in.author() != null ? in.author() : "anonymous",
                in.name() != null ? in.name() : "nuevo",
                in.points() != null ? in.points() : List.of()
        );

        storage.put(id, blueprint);
        return ResponseEntity.ok(blueprint);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.update')")
    public ResponseEntity<Blueprint> update(@PathVariable String id, @RequestBody BlueprintRequest in) {
        Blueprint existing = storage.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        Blueprint updated = new Blueprint(
                id,
                in.author() != null ? in.author() : existing.author(),
                in.name() != null ? in.name() : existing.name(),
                in.points() != null ? in.points() : existing.points()
        );

        storage.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.delete')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        Blueprint removed = storage.remove(id);
        if (removed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("deleted", id));
    }
}

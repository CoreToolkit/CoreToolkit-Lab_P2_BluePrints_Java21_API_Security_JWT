package co.edu.eci.blueprints.api;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static co.edu.eci.blueprints.api.BlueprintModels.Blueprint;
import static co.edu.eci.blueprints.api.BlueprintModels.BlueprintRequest;
import static co.edu.eci.blueprints.api.BlueprintModels.Point;

@Service
public class BlueprintRepository {

    private final Map<String, Blueprint> storage = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    public BlueprintRepository() {
        saveInitial("student", "Casa de campo", List.of(
                new Point(30, 220),
                new Point(30, 120),
                new Point(120, 120),
                new Point(120, 220),
                new Point(30, 220)
        ));
        saveInitial("student", "Edificio urbano", List.of(
                new Point(180, 240),
                new Point(180, 80),
                new Point(260, 80),
                new Point(260, 240)
        ));
    }

    public List<Blueprint> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Blueprint::author).thenComparing(Blueprint::name))
                .toList();
    }

    public List<Blueprint> findByAuthor(String author) {
        return storage.values().stream()
                .filter(bp -> bp.author().equalsIgnoreCase(author))
                .sorted(Comparator.comparing(Blueprint::name))
                .toList();
    }

    public Blueprint findByAuthorAndName(String author, String name) {
        return storage.get(key(author, name));
    }

    public Blueprint create(BlueprintRequest request) {
        String author = normalize(request.author(), "anonymous");
        String name = normalize(request.name(), "nuevo");
        Blueprint blueprint = new Blueprint(
                nextId(),
                author,
                name,
                copyPoints(request.points())
        );
        storage.put(key(author, name), blueprint);
        return blueprint;
    }

    public Blueprint update(String author, String name, BlueprintRequest request) {
        Blueprint existing = findByAuthorAndName(author, name);
        if (existing == null) {
            return null;
        }

        String nextAuthor = normalize(request.author(), existing.author());
        String nextName = normalize(request.name(), existing.name());
        Blueprint updated = new Blueprint(
                existing.id(),
                nextAuthor,
                nextName,
                request.points() != null ? copyPoints(request.points()) : existing.points()
        );

        storage.remove(key(author, name));
        storage.put(key(nextAuthor, nextName), updated);
        return updated;
    }

    public Blueprint delete(String author, String name) {
        return storage.remove(key(author, name));
    }

    private void saveInitial(String author, String name, List<Point> points) {
        Blueprint blueprint = new Blueprint(nextId(), author, name, copyPoints(points));
        storage.put(key(author, name), blueprint);
    }

    private String nextId() {
        return "b" + sequence.incrementAndGet();
    }

    private String key(String author, String name) {
        return normalize(author, "").toLowerCase() + "::" + normalize(name, "").toLowerCase();
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private List<Point> copyPoints(List<Point> points) {
        if (points == null) {
            return List.of();
        }
        return new ArrayList<>(points);
    }
}

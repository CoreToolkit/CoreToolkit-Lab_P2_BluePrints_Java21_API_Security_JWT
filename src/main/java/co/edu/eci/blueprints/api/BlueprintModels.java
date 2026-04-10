package co.edu.eci.blueprints.api;

import java.util.List;

public final class BlueprintModels {

    private BlueprintModels() {
    }

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
}

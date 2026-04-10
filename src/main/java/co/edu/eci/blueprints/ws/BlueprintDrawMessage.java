package co.edu.eci.blueprints.ws;

import co.edu.eci.blueprints.api.BlueprintModels.Point;

public record BlueprintDrawMessage(
        String author,
        String name,
        Point point
) {
}

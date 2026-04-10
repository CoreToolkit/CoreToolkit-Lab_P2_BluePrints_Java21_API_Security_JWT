package co.edu.eci.blueprints.ws;

import co.edu.eci.blueprints.api.BlueprintModels.Blueprint;
import co.edu.eci.blueprints.api.BlueprintModels.BlueprintRequest;
import co.edu.eci.blueprints.api.BlueprintModels.Point;
import co.edu.eci.blueprints.api.BlueprintRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BlueprintSocketController {

    private final BlueprintRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public BlueprintSocketController(BlueprintRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/draw")
    public void draw(BlueprintDrawMessage message) {
        if (message == null || message.author() == null || message.name() == null || message.point() == null) {
            return;
        }

        Blueprint current = repository.findByAuthorAndName(message.author(), message.name());
        if (current == null) {
            current = repository.create(new BlueprintRequest(message.author(), message.name(), List.of()));
        }

        List<Point> nextPoints = new ArrayList<>(current.points());
        nextPoints.add(message.point());

        Blueprint updated = repository.update(
                current.author(),
                current.name(),
                new BlueprintRequest(current.author(), current.name(), nextPoints)
        );

        messagingTemplate.convertAndSend(
                "/topic/blueprints." + updated.author() + "." + updated.name(),
                updated
        );
    }
}

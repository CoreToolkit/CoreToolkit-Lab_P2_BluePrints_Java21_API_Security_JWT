package co.edu.eci.blueprints.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static co.edu.eci.blueprints.api.BlueprintModels.Blueprint;
import static co.edu.eci.blueprints.api.BlueprintModels.BlueprintRequest;

@RestController
@RequestMapping("/api/blueprints")
public class BlueprintController {

    private final BlueprintRepository repository;

    public BlueprintController(BlueprintRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Blueprint> list(@RequestParam(required = false) String author) {
        if (author == null || author.isBlank()) {
            return repository.findAll();
        }
        return repository.findByAuthor(author);
    }

    @GetMapping("/{author}/{name}")
    public ResponseEntity<Blueprint> get(@PathVariable String author, @PathVariable String name) {
        Blueprint blueprint = repository.findByAuthorAndName(author, name);
        if (blueprint == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blueprint);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public ResponseEntity<Blueprint> create(@RequestBody BlueprintRequest request) {
        Blueprint blueprint = repository.create(request);
        return ResponseEntity.ok(blueprint);
    }

    @PutMapping("/{author}/{name}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.update')")
    public ResponseEntity<Blueprint> update(
            @PathVariable String author,
            @PathVariable String name,
            @RequestBody BlueprintRequest request
    ) {
        Blueprint updated = repository.update(author, name, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{author}/{name}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.delete')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String author, @PathVariable String name) {
        Blueprint removed = repository.delete(author, name);
        if (removed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("deleted", removed.name(), "author", removed.author()));
    }
}

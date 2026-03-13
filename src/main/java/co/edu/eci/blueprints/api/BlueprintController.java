package co.edu.eci.blueprints.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blueprints")
public class BlueprintController {

    //Read all
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public List<Map<String, String>> list() {
        return List.of(
            Map.of("id", "b1", "name", "Casa de campo"),
            Map.of("id", "b2", "name", "Edificio urbano")
        );
    }

    //Read one
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public Map<String, String> get(@PathVariable String id) {
        return Map.of("id", id, "name", "Blueprint " + id);
    }

    //Create
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public Map<String, String> create(@RequestBody Map<String, String> in) {
        return Map.of("id", "new", "name", in.getOrDefault("name", "nuevo"));
    }

    //Update
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.update')")
    public Map<String, String> update(@PathVariable String id, @RequestBody Map<String, String> in) {
        return Map.of("id", id, "name", in.getOrDefault("name", "updated"));
    }

    //Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.delete')")
    public Map<String, String> delete(@PathVariable String id) {
        return Map.of("deleted", id);
    }
}
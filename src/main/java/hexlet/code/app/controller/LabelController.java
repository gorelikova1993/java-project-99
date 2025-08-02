package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getById(@PathVariable Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
        return ResponseEntity.ok(labelMapper.toDto(label));
    }
    @GetMapping
    public ResponseEntity<List<LabelDTO>> getAll() {
        List<LabelDTO> labels = labelRepository.findAll().stream()
                .map(labelMapper::toDto)
                .toList();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(labels.size()));
        headers.add("Access-Control-Expose-Headers", "X-Total-Count"); // важно для CORS
        
        return new ResponseEntity<>(labels, headers, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<LabelDTO> create(@Valid @RequestBody LabelCreateDTO dto) {
        Label label = new Label();
        label.setName(dto.getName());
        label.setCreatedAt(LocalDate.now());
        labelRepository.save(label);
        return new ResponseEntity<>(labelMapper.toDto(label), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO dto) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
        label.setName(dto.getName());
        labelRepository.save(label);
        return ResponseEntity.ok(labelMapper.toDto(label));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
        labelRepository.delete(label);
        return ResponseEntity.noContent().build();
    }
}

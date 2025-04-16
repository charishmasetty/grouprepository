package edu.gmu.swe645.student_survey_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surveys")
public class StudentSurveyController {

    private final StudentSurveyRepository repo;

    public StudentSurveyController(StudentSurveyRepository repo) {
        this.repo = repo;
    }

    // CREATE
    @PostMapping
    public StudentSurvey create(@RequestBody StudentSurvey survey) {
        return repo.save(survey);
    }

    // READ ALL
    @GetMapping
    public List<StudentSurvey> getAll() {
        System.out.println("Jenkins build trigger change: getAll endpoint invoked!");
        return repo.findAll();
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<StudentSurvey> getOne(@PathVariable Long id) {
        return repo.findById(id)
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<StudentSurvey> update(@PathVariable Long id,
                                                @RequestBody StudentSurvey s) {
        return repo.findById(id).map(old -> {
            // copy fields
            old.setFirstName(s.getFirstName());
            old.setLastName(s.getLastName());
            old.setStreetAddress(s.getStreetAddress());
            old.setCity(s.getCity());
            old.setState(s.getState());
            old.setZip(s.getZip());
            old.setTelephone(s.getTelephone());
            old.setEmail(s.getEmail());
            old.setDateOfSurvey(s.getDateOfSurvey());
            old.setLikedMost(s.getLikedMost());
            old.setInterestSource(s.getInterestSource());
            old.setRecommendation(s.getRecommendation());
            return ResponseEntity.ok(repo.save(old));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repo.findById(id).map(s -> {
            repo.delete(s);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
    
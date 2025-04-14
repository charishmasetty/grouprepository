package edu.gmu.swe645.student_survey_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentSurveyRepository
        extends JpaRepository<StudentSurvey, Long> {
}

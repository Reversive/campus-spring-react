package ar.edu.itba.paw.interfaces;

import ar.edu.itba.paw.models.Exam;
import ar.edu.itba.paw.models.FileModel;

import java.sql.Time;
import java.util.List;
import java.util.Optional;


public interface ExamDao {

    Exam create(Long courseId, String title, String description, FileModel examFile, FileModel answersFile, Time startTime, Time endTime);

    boolean update(Long examId, Exam exam);

    boolean delete(Long examId);

    List<Exam> list(Long courseId);

    Optional<Exam> findById(Long examId);

}

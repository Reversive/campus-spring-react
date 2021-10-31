package ar.edu.itba.paw.interfaces;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Exam;
import ar.edu.itba.paw.models.User;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AnswerService {
    /**
     * Attempts to persist an answer entry in the data base
     *
     * @param examId       id of the exam that was resolved
     * @param studentId    id of the student that resolves the exam
     * @param answerFile   the answer given in a file
     * @param answerFileSize the size of the file
     * @param deliveredTime the last sending
     * @return an Answer instance holding the passed values
     */
    Answer create(Long examId, Long studentId, byte[] answerFile, Long answerFileSize, LocalDateTime deliveredTime);

    /**
     * Attempts to update an answer
     *
     * @param answerId of the answer to be modified
     * @param answer   modified answer
     * @return true if the answer was successfully update, false otherwise
     */
    boolean update(Long answerId, Answer answer);

    /**
     * Attempts to delete an answer
     *
     * @param answerId of the answer to be deleted
     * @return true if the answer was successfully removed, false otherwise
     */

    boolean delete(Long answerId);

    /**
     * Attempts to get an answer given an id
     *
     * @param answerId of the answer to be retrieved
     * @return an option with the answer corresponding to the given id if exists, else an empty optional
     */

    Optional<Answer> findById(Long answerId);

    /**
     * Attempts to return the number of answers for a exam
     *
     * @param examId of the queried exam
     * @return a number that represents the total of answers
     */
    Long getTotalResolvedByExam(Long examId);

    /**
     * Attempts to persist in the database a correction for an exam
     *
     * @param answerId of the answer that is being corrected
     * @param teacher  that corrects that answer
     * @param score    the score given
     */

    void correctExam(Long answerId, User teacher, Float score);

    /**
     * Attempts to retrieved all the corrected exams for a course
     *
     * @param examId of the queried exam
     * @return a list of all the exams that are already corrected
     */
    List<Answer> getCorrectedAnswers(Long examId);

    /**
     * Attempts to retrieved all the not corrected exams for a course
     *
     * @param examId of the queried exam
     * @return a list of all the exams that are not already corrected
     */
    List<Answer> getNotCorrectedAnswers(Long examId);

    /**
     * Attempts to delete a correction to an answer
     *
     * @param answerId of the answer to be deleted the correction
     */
    void undoExamCorrection(Long answerId);

    /**
     * Attempts to return a list of the resolved exams by the user
     *
     * @param studentId of the queried user
     * @return a list of all the exams resolved by the user
     */
    List<Exam> getResolvedExams(Long studentId,Long courseId);

    /**
     * Attempts to return a list of the unresolved exams by the user
     *
     * @param studentId of the queried user
     * @return a list of all the exams unresolved by the user
     */
    List<Exam> getUnresolvedExams(Long studentId,Long courseId);

    /**
     * Attempts to return the number of answers to an exam
     *
     * @param examId of the queried exam
     * @return the number of answers for that exam
     */
    Long getTotalAnswers(Long examId);

    /**
     * Attempts to return the number of corrected answers to an exam
     *
     * @param examId of the queried exam
     * @return the number of corrected answers to an exam
     */
    Long getTotalCorrectedAnswers(Long examId);

    /**
     * Attempts to return a map of exams with the number of answers and corrected answers
     * @param courseId of the queried course
     * @return a map with an Exam as a key and a Pair<Long,Long> where the first is totalAnswers and the second is totalCorrectedAnswers
     */
    Map<Exam, Pair<Long,Long>> getExamsAndTotals(Long courseId);
}

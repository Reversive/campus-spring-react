package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.CourseDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exception.PaginationArgumentException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Primary
@Repository
public class CourseDaoJpa extends BasePaginationDaoImpl<Course> implements CourseDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public Course create(Integer year, Integer quarter, String board, Long subjectId) {
        final Course course = new Course(year, quarter, board, new Subject(subjectId, null, null));
        em.persist(course);
        return course;
    }

    @Transactional
    @Override
    public boolean update(Long id, Course course) {
        Optional<Course> dbCourse = findById(id);
        if (!dbCourse.isPresent()) return false;
        dbCourse.get().merge(course);
        em.flush();
        return true;
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        Optional<Course> dbCourse = findById(id);
        if (!dbCourse.isPresent()) return false;
        em.remove(dbCourse.get());
        return true;
    }

    @Override
    public List<Course> list() {
        TypedQuery<Course> listCoursesTypedQuery = em.createQuery("SELECT c From Course c", Course.class);
        return listCoursesTypedQuery.getResultList();
    }

    @Override
    public CampusPage<Course> list(Long userId, CampusPageRequest pageRequest) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("userId", userId);
        String query = "SELECT courseId FROM courses NATURAL JOIN user_to_course WHERE userId = :userId ORDER BY year DESC";
        String mappingQuery = "SELECT DISTINCT enrollment.course FROM Enrollment enrollment WHERE enrollment.course.courseId IN (:ids) ORDER BY enrollment.course.year DESC";
        return listBy(properties, query, mappingQuery, pageRequest, Course.class);
    }


    @Override
    public List<Course> listCurrent(Long userId) {
        LocalDateTime time = LocalDateTime.now();
        int quarter = time.getMonthValue() >= Month.JULY.getValue() ? 2 : 1;
        int year = time.getYear();
        TypedQuery<Course> listCoursesTypedQuery = em.createQuery("SELECT enrollment.course FROM Enrollment enrollment WHERE enrollment.user.userId = :userId AND enrollment.course.quarter = :quarter AND enrollment.course.year =:year", Course.class);
        listCoursesTypedQuery.setParameter("userId", userId);
        listCoursesTypedQuery.setParameter("quarter", quarter);
        listCoursesTypedQuery.setParameter("year", year);
        return listCoursesTypedQuery.getResultList();
    }

    @Override
    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(em.find(Course.class, id));
    }

    @Override
    public List<User> getStudents(Long courseId) {
        TypedQuery<User> listUserTypedQuery = em.createQuery("SELECT enrollment.user FROM Enrollment enrollment  WHERE enrollment.course.courseId =:courseId AND enrollment.role.roleId=:roleId", User.class);
        listUserTypedQuery.setParameter("courseId", courseId);
        listUserTypedQuery.setParameter("roleId", Roles.STUDENT.getValue());
        return listUserTypedQuery.getResultList();
    }

    @Override
    public Map<User, Role> getTeachers(Long courseId) {
        return new HashMap<>(); //TODO
    }

    @Override
    public boolean belongs(Long userId, Long courseId) {
        TypedQuery<Course> courseTypedQuery = em.createQuery("SELECT enrollment.course FROM Enrollment enrollment WHERE enrollment.course.courseId=:courseId AND enrollment.user.userId = :userId", Course.class);
        courseTypedQuery.setParameter("userId", userId);
        courseTypedQuery.setParameter("courseId", courseId);
        return courseTypedQuery.getResultList().isEmpty();
    }

    @Transactional
    @Override
    public boolean enroll(Long userId, Long courseId, Integer roleId) {
        Enrollment enrollment = new Enrollment(em.find(User.class, userId), em.find(Course.class, courseId), em.find(Role.class, roleId));
        em.persist(enrollment);
        return true;
    }

    @Override
    public List<User> listUnenrolledUsers(Long courseId) {
        TypedQuery<User> listUserTypedQuery = em.createQuery("SELECT user FROM User user WHERE user.admin = false AND user.userId NOT IN (SELECT enrollment.user.userId FROM  Enrollment enrollment WHERE enrollment.course.courseId = :courseId)", User.class);
        listUserTypedQuery.setParameter("courseId", courseId);
        return listUserTypedQuery.getResultList();
    }

    @Override
    public List<Course> listWhereStudent(Long userId) {
        TypedQuery<Course> listCourseTypedQuery = em.createQuery("SELECT enrollment.course FROM Enrollment enrollment WHERE enrollment.user.userId = :userId AND enrollment.role.roleId = :roleId", Course.class);
        listCourseTypedQuery.setParameter("userId", userId);
        listCourseTypedQuery.setParameter("roleId", Roles.STUDENT.getValue());
        return listCourseTypedQuery.getResultList();
    }

    @Override
    public List<Course> listByYearQuarter(Integer year, Integer quarter) {
        TypedQuery<Course> listCourseTypedQuery = em.createQuery("SELECT course FROM Course course WHERE course.year =:year AND course.quarter = :quarter", Course.class);
        listCourseTypedQuery.setParameter("year", year);
        listCourseTypedQuery.setParameter("quarter", quarter);
        return listCourseTypedQuery.getResultList();
    }

    @Override
    public List<Integer> getAvailableYears() {
        TypedQuery<Integer> listYearsTypedQuery = em.createQuery("SELECT DISTINCT course.year FROM Course course", Integer.class);
        return listYearsTypedQuery.getResultList();
    }
}

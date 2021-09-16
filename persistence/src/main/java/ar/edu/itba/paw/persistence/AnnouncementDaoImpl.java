package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.AnnouncementDao;
import ar.edu.itba.paw.models.Announcement;
import ar.edu.itba.paw.models.Course;
import ar.edu.itba.paw.models.Subject;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class AnnouncementDaoImpl implements AnnouncementDao {

    private JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private static final RowMapper<Announcement> COURSE_ANNOUNCEMENT_ROW_MAPPER = (rs, rowNum) ->
        new Announcement.Builder()
            .withAnnouncementId(rs.getLong("announcementid"))
            .withDate(rs.getTimestamp("date").toLocalDateTime())
            .withTitle(rs.getString("title"))
            .withContent(rs.getString("content"))
            .withAuthor(new User.Builder()
                    .withUserId(rs.getLong("userId"))
                    .withFileNumber(rs.getInt("fileNumber"))
                    .withName(rs.getString("name"))
                    .withSurname(rs.getString("surname"))
                    .withUsername(rs.getString("username"))
                    .withEmail(rs.getString("email"))
                    .withPassword(rs.getString("password"))
                    .isAdmin(rs.getBoolean("isAdmin"))
                    .build())
            .withCourse(new Course.Builder()
                    .withCourseId(rs.getLong("courseId"))
                    .withYear(rs.getInt("year"))
                    .withQuarter(rs.getInt("quarter"))
                    .withBoard(rs.getString("board"))
                    .withSubject(new Subject(rs.getInt("subjectId"), rs.getString("code"),
                            rs.getString("subjectName")))
                    .build())
            .build();

    private static final RowMapper<Announcement> ANNOUNCEMENT_ROW_MAPPER = (rs, rowNum) ->
            new Announcement.Builder()
                    .withAnnouncementId(rs.getLong("announcementid"))
                    .withDate(rs.getTimestamp("date").toLocalDateTime())
                    .withTitle(rs.getString("title"))
                    .withContent(rs.getString("content"))
                    .withAuthor(new User.Builder()
                            .withUserId(rs.getLong("userId"))
                            .withFileNumber(rs.getInt("fileNumber"))
                            .withName(rs.getString("name"))
                            .withSurname(rs.getString("surname"))
                            .withUsername(rs.getString("username"))
                            .withEmail(rs.getString("email"))
                            .withPassword(rs.getString("password"))
                            .isAdmin(rs.getBoolean("isAdmin"))
                            .build())
                    .withCourse(null)
            .build();

    @Autowired
    public AnnouncementDaoImpl(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("announcements").usingGeneratedKeyColumns("announcementid");
    }

    @Override
    public Announcement create(LocalDateTime date, String title, String content, User author, Course course) {
        final Map<String, Object> args = new HashMap<>();
        args.put("date", Timestamp.valueOf(date));
        args.put("title",title);
        args.put("content", content);
        args.put("userId", author.getUserId());
        args.put("courseId", course.getCourseId());
        final Long announcementId = jdbcInsert.executeAndReturnKey(args).longValue();
        return new Announcement.Builder()
            .withAnnouncementId(announcementId)
            .withDate(date)
            .withTitle(title)
            .withContent(content)
            .withAuthor(author)
            .withCourse(course)
        .build();
    }

    @Override
    public boolean update(Long id, Announcement announcement) {
        return jdbcTemplate.update("UPDATE announcements " +
                "SET userId = ?," +
                "courseId = ?," +
                "date = ?," +
                "title = ?," +
                "content = ?" +
                "WHERE announcementId = ?", new Object[]{announcement.getAuthor().getUserId(), announcement.getCourse().getCourseId(),
                Timestamp.valueOf(announcement.getDate()), announcement.getTitle(), announcement.getContent(), id}) == 1;
    }

    @Override
    public boolean delete(Long id) {
        return jdbcTemplate.update("DELETE FROM announcements WHERE announcementId = ?", new Object[]{id}) == 1;
    }

    @Override
    public int getPageCount(Integer pageSize) {
        RowCountCallbackHandler countCallback = new RowCountCallbackHandler();  // not reusable
        jdbcTemplate.query("SELECT * FROM announcements", countCallback);
        return (int) Math.ceil((double)countCallback.getRowCount() / pageSize);
    }

    @Override
    public List<Announcement> list(Long userId, Integer page, Integer pageSize) {
        return new ArrayList<>(jdbcTemplate.query(
                "SELECT * FROM announcements NATURAL JOIN courses NATURAL JOIN subjects NATURAL JOIN users " +
                "NATURAL JOIN user_to_course " +
                "WHERE courseid IN (SELECT courseid FROM user_to_course WHERE userid = ?) " +
                "LIMIT ? OFFSET ?",new Object[]{ userId, pageSize, (page - 1) * pageSize }, COURSE_ANNOUNCEMENT_ROW_MAPPER));
    }


    public List<Announcement> listByCourse(Long courseId) {
        return new ArrayList<>(jdbcTemplate.query("SELECT * FROM announcements NATURAL JOIN users WHERE courseId = ?",
                new Object[]{courseId}, ANNOUNCEMENT_ROW_MAPPER));
    }

    @Override
    public Optional<Announcement> getById(Long id) {
        return jdbcTemplate.query("SELECT * " +
                "FROM announcements NATURAL JOIN courses NATURAL JOIN subjects NATURAL JOIN users " +
                "WHERE announcementId = ?", new Object[]{id}, COURSE_ANNOUNCEMENT_ROW_MAPPER).stream().findFirst();
    }
}

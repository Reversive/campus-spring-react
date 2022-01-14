package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.CourseService;
import ar.edu.itba.paw.interfaces.RoleService;
import ar.edu.itba.paw.interfaces.SubjectService;
import ar.edu.itba.paw.interfaces.UserService;
import ar.edu.itba.paw.models.Course;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.constraint.validator.DtoConstraintValidator;
import ar.edu.itba.paw.webapp.dto.CourseFormDto;
import ar.edu.itba.paw.webapp.dto.UserRegisterFormDto;
import ar.edu.itba.paw.webapp.form.CourseForm;
import ar.edu.itba.paw.webapp.security.api.exception.DtoValidationException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("admin")
@Component
@ComponentScan({"ar.edu.itba.paw.webapp.constraint.validator"}) //TODO: ver si esto se puede sacar, se lo puse por que sino no me encontraba el bean DtoConstraintValidator
public class AdminControllerREST {

    @Autowired
    private DtoConstraintValidator DtoValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private RoleService roleService;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AdminController.class);


    @POST
    @Path("/user/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = {MediaType.APPLICATION_JSON, })
    public Response newUser(@Valid UserRegisterFormDto userRegisterForm) throws DtoValidationException {

        if (userRegisterForm != null) {
            DtoValidator.validate(userRegisterForm, "Failed to validate new user attributes");
            User user = userService.create(userRegisterForm.getFileNumber(), userRegisterForm.getName(), userRegisterForm.getSurname(),
                    userRegisterForm.getUsername(), userRegisterForm.getEmail(),
                    userRegisterForm.getPassword(), false);
            LOGGER.debug("User of name {} created", user.getUsername());
            return Response.ok(new GenericEntity<User>(user) {
            }).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/course/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = {MediaType.APPLICATION_JSON, })
    public Response newCourse(@Valid CourseFormDto courseForm) throws DtoValidationException { //TODO: ver bien como seria la parte del json para startTimes y endTimes
        if(courseForm != null) {
            Course course = courseService.create(courseForm.getYear(), courseForm.getQuarter(), courseForm.getBoard()
                    , courseForm.getSubjectId(), courseForm.getStartTimes(), courseForm.getEndTimes());
            LOGGER.debug("Created course in year {} in quarter {} of subjectId {} with id {}", courseForm.getYear(),
                    courseForm.getBoard(), courseForm.getSubjectId(), course.getCourseId());
            return Response.ok(new GenericEntity<Course>(course) {
            }).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}

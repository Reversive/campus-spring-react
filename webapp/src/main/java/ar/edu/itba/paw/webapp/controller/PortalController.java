package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.interfaces.CourseService;
import ar.edu.itba.paw.interfaces.SubjectService;
import ar.edu.itba.paw.models.Course;
import ar.edu.itba.paw.webapp.auth.AuthFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import java.util.List;

@Controller
public class PortalController extends AuthController{

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalController.class);

    @Autowired
    CourseService courseService;

    @Autowired
    AuthFacade authFacade;

    @Autowired
    SubjectService subjectService;

    @RequestMapping("/")
    public RedirectView rootRedirect() {
        return new RedirectView("portal");
    }

    @RequestMapping("/portal")
    public ModelAndView portal() {
        LOGGER.debug("Log debug call from {}", PortalController.class.getSimpleName());
        ModelAndView mav = new ModelAndView("portal");
        List<Course> courses = courseService.list(authFacade.getCurrentUser().getUserId());
        mav.addObject("courseList", courses);
        return mav;
    }

}

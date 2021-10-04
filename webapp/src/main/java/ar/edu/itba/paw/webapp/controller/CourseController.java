package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.AuthFacade;
import ar.edu.itba.paw.webapp.auth.CampusUser;
import ar.edu.itba.paw.webapp.exception.CourseNotFoundException;
import ar.edu.itba.paw.webapp.form.AnnouncementForm;
import ar.edu.itba.paw.webapp.form.FileForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping(value = "/course")
public class CourseController extends AuthController {
    private final AnnouncementService announcementService;
    private final CourseService courseService;
    private final FileCategoryService fileCategoryService;
    private final FileExtensionService fileExtensionService;
    private final FileService fileService;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    public CourseController(AuthFacade authFacade, AnnouncementService announcementService,
                            CourseService courseService, FileCategoryService fileCategoryService,
                            FileExtensionService fileExtensionService, FileService fileService) {
        super(authFacade);
        this.announcementService = announcementService;
        this.courseService = courseService;
        this.fileCategoryService = fileCategoryService;
        this.fileExtensionService = fileExtensionService;
        this.fileService = fileService;
    }

    @GetMapping(value = "/{courseId}")
    public String coursePortal(@PathVariable Integer courseId) {
       return "redirect:/course/{courseId}/announcements";

    }

    @GetMapping(value = "/{courseId}/announcements")
    public ModelAndView announcements(@PathVariable Long courseId, final AnnouncementForm announcementForm,
                                      String successMessage,
                                      @RequestParam(value = "page", required = false, defaultValue = "1")
                                                  Integer page,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10")
                                                  Integer pageSize) {
        final ModelAndView mav;
        if (courseService.isPrivileged(authFacade.getCurrentUser().getUserId(), courseId)) {
            mav = new ModelAndView("teacher/teacher-course");
            mav.addObject("announcementForm", announcementForm);
            mav.addObject("successMessage", successMessage);
        } else {
            mav = new ModelAndView("course");
        }
        mav.addObject("course", courseService.getById(courseId).orElseThrow(CourseNotFoundException::new));
        CampusPage<Announcement> announcements = announcementService.listByCourse(courseId, new CampusPageRequest(page, pageSize));
        mav.addObject("announcementList", announcements.getContent());
        mav.addObject("dateTimeFormatter",dateTimeFormatter);
        return mav;
    }

    @PostMapping(value = "/{courseId}/announcements")
    public ModelAndView postAnnouncement(@PathVariable Long courseId,
                                         @Valid AnnouncementForm announcementForm, final BindingResult errors) {
        String successMessage = null;
        if (!errors.hasErrors()) {
            CampusUser springUser = authFacade.getCurrentUser();
            User currentUser = new User.Builder()
                    .withUserId(springUser.getUserId())
                    .withEmail(springUser.getEmail())
                    .withFileNumber(springUser.getFileNumber())
                    .withPassword(springUser.getPassword())
                    .withName(springUser.getName())
                    .withSurname(springUser.getSurname())
                    .withUsername(springUser.getUsername())
                    .withProfileImage(springUser.getImage())
                    .build();
            announcementService.create(announcementForm.getTitle(), announcementForm.getContent(), currentUser,
                    courseService.getById(courseId).orElseThrow(CourseNotFoundException::new));
            announcementForm.setContent("");
            announcementForm.setTitle("");
            successMessage = "announcement.success.message";
        }
        return announcements(courseId, announcementForm, successMessage, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    @GetMapping("/{courseId}/teachers")
    public ModelAndView professors(@PathVariable Long courseId) {
        final ModelAndView mav = new ModelAndView("teachers");
        mav.addObject("course", courseService.getById(courseId).orElseThrow(CourseNotFoundException::new));
        Map<User, Role> teachers = courseService.getTeachers(courseId);
        Set<Map.Entry<User, Role>> teacherSet = teachers.entrySet();
        mav.addObject("teacherSet", teacherSet);
        return mav;
    }

    @GetMapping(value = "/{courseId}/files")
    public ModelAndView files(@PathVariable Long courseId, final FileForm fileForm, String successMessage,
                              @RequestParam(value = "category-type", required = false, defaultValue = "")
                                      List<Long> categoryType,
                              @RequestParam(value = "extension-type", required = false, defaultValue = "")
                                      List<Long> extensionType,
                              @RequestParam(value = "query", required = false, defaultValue = "")
                                      String query,
                              @RequestParam(value = "order-property", required = false, defaultValue = "date")
                                      String orderProperty,
                              @RequestParam(value = "order-direction", required = false, defaultValue = "desc")
                                      String orderDirection,
                              @RequestParam(value = "page", required = false, defaultValue = "1")
                                      Integer page,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10")
                                      Integer pageSize) {
        CampusUser user = authFacade.getCurrentUser();
        CampusPage<FileModel> filePage = fileService.listByCourse(query, extensionType, categoryType, user.getUserId(),
                courseId, new CampusPageRequest(page, pageSize), new CampusPageSort(orderDirection, orderProperty));
        final ModelAndView mav;
        final List<FileCategory> categories = fileCategoryService.getCategories();
        final List<FileExtension> extensions = fileExtensionService.getExtensions();
        if (courseService.isPrivileged(user.getUserId(), courseId)) {
            mav = new ModelAndView("teacher/teacher-files");
            mav.addObject("fileForm", fileForm);
            mav.addObject("successMessage", successMessage);
        } else {
            mav = new ModelAndView("course-files");
        }
        mav.addObject("course", courseService.getById(courseId).orElseThrow(CourseNotFoundException::new));
        mav.addObject("categories", categories);
        mav.addObject("files", filePage.getContent());
        mav.addObject("extensions", extensions);
        return FilesController.loadFileParamsIntoModel(categoryType, extensionType, query, orderProperty, orderDirection, filePage, mav);
    }

    @PostMapping(value = "/{courseId}/files")
    public ModelAndView uploadFile(@PathVariable Long courseId, @Valid FileForm fileForm, final BindingResult errors) {
        String successMessage = null;
        if (!errors.hasErrors()) {
            CommonsMultipartFile file = fileForm.getFile();
            String filename = file.getOriginalFilename();
            // Function is expanded already for multiple categories in the future, passing only one for now
            fileService.create(file.getSize(), filename, file.getBytes(),
                    courseService.getById(courseId).orElseThrow(CourseNotFoundException::new),
                    Collections.singletonList(fileForm.getCategoryId()));
            fileForm.setFile(null);
            fileForm.setCategoryId(null);
            successMessage = "file.success.message";
        }
        return files(courseId, fileForm, successMessage, new ArrayList<>(),
                new ArrayList<>(), "", "date", "desc", DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

}

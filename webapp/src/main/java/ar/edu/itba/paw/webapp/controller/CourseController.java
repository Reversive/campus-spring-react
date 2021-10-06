package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exception.CourseNotFoundException;
import ar.edu.itba.paw.models.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.auth.AuthFacade;
import ar.edu.itba.paw.webapp.form.AnnouncementForm;
import ar.edu.itba.paw.webapp.form.FileForm;
import ar.edu.itba.paw.webapp.form.MailForm;
import com.sun.media.jfxmedia.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping(value = "/course/{courseId}")
public class CourseController extends AuthController {
    private final AnnouncementService announcementService;
    private final CourseService courseService;
    private final FileCategoryService fileCategoryService;
    private final FileExtensionService fileExtensionService;
    private final FileService fileService;
    private final UserService userService;
    private final MailingService mailingService;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    public CourseController(AuthFacade authFacade, AnnouncementService announcementService,
                            CourseService courseService, FileCategoryService fileCategoryService,
                            FileExtensionService fileExtensionService, FileService fileService,
                            UserService userService, MailingService mailingService) {
        super(authFacade);
        this.announcementService = announcementService;
        this.courseService = courseService;
        this.fileCategoryService = fileCategoryService;
        this.fileExtensionService = fileExtensionService;
        this.fileService = fileService;
        this.userService = userService;
        this.mailingService = mailingService;
    }

    @ModelAttribute
    public void getCourse(Model model, @PathVariable Long courseId) {
        model.addAttribute("course",
                courseService.findById(courseId).orElseThrow(CourseNotFoundException::new));
    }

    @GetMapping(value = "")
    public String coursePortal(@PathVariable Long courseId) {
       return "redirect:/course/{courseId}/announcements";

    }

    @GetMapping(value = "/announcements")
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
        CampusPage<Announcement> announcements = announcementService.listByCourse(courseId, page, pageSize);
        mav.addObject("announcementList", announcements.getContent());
        mav.addObject("dateTimeFormatter",dateTimeFormatter);
        return mav;
    }

    @PostMapping(value = "/announcements")
    public ModelAndView postAnnouncement(@PathVariable Long courseId,
                                         @Valid AnnouncementForm announcementForm, final BindingResult errors) {
        String successMessage = null;
        if (!errors.hasErrors()) {
            Announcement announcement =announcementService.create(announcementForm.getTitle(), announcementForm.getContent(),
                    authFacade.getCurrentUser(), courseService.findById(courseId).orElseThrow(CourseNotFoundException::new));
            LOGGER.debug("Announcement in course " + courseId +
                    " created with id: " + announcement.getAnnouncementId());
            announcementForm.setContent("");
            announcementForm.setTitle("");
            successMessage = "announcement.success.message";
        }
        return announcements(courseId, announcementForm, successMessage, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    @GetMapping("/teachers")
    public ModelAndView teachers(@PathVariable Long courseId) {
        final ModelAndView mav = new ModelAndView("teachers");
        mav.addObject("teacherSet", courseService.getTeachers(courseId).entrySet());
        return mav;
    }

    @GetMapping(value = "/files")
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
        CampusPage<FileModel> filePage = fileService.listByCourse(query, extensionType, categoryType,
                authFacade.getCurrentUserId(), courseId, page, pageSize, orderDirection, orderProperty);
        final ModelAndView mav;
        if (courseService.isPrivileged(authFacade.getCurrentUserId(), courseId)) {
            mav = new ModelAndView("teacher/teacher-files");
            mav.addObject("fileForm", fileForm);
            mav.addObject("successMessage", successMessage);
        } else {
            mav = new ModelAndView("course-files");
        }
        mav.addObject("categories", fileCategoryService.getCategories());
        mav.addObject("files", filePage.getContent());
        mav.addObject("extensions", fileExtensionService.getExtensions());
        return FilesController.loadFileParamsIntoModel(categoryType, extensionType, query, orderProperty, orderDirection, filePage, mav);
    }

    @PostMapping(value = "/files")
    public ModelAndView uploadFile(@PathVariable Long courseId, @Valid FileForm fileForm, final BindingResult errors) {
        String successMessage = null;
        if (!errors.hasErrors()) {
            CommonsMultipartFile file = fileForm.getFile();
            // Function is expanded already for multiple categories in the future, passing only one for now
            FileModel createdFile = fileService.create(file.getSize(), file.getOriginalFilename(), file.getBytes(),
                    courseService.findById(courseId).orElseThrow(CourseNotFoundException::new),
                    Collections.singletonList(fileForm.getCategoryId()));
            LOGGER.debug("File in course " + courseId +
                    " created with id: " + createdFile.getFileId());
            fileForm.setFile(null);
            fileForm.setCategoryId(null);
            successMessage = "file.success.message";
        }
        return files(courseId, fileForm, successMessage, new ArrayList<>(),
                new ArrayList<>(), "", "date", "desc", DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    @GetMapping(value = "/mail/{userId}")
    public ModelAndView sendMail(@PathVariable final Long courseId, @PathVariable final Long userId,
                                 final MailForm mailForm) {
        if(!courseService.belongs(userId, courseId) && !courseService.isPrivileged(userId, courseId)) throw new UserNotFoundException();
        ModelAndView mav = new ModelAndView("sendmail");
        mav.addObject("user", userService.findById(userId).orElseThrow(UserNotFoundException::new));
        mav.addObject("mailForm",mailForm);
        return mav;
    }

    @PostMapping(value = "/mail/{userId}")
    public ModelAndView sendMail(@PathVariable Long courseId, @PathVariable Long userId,
                                 @Valid MailForm mailForm, final BindingResult errors,
                                 RedirectAttributes redirectAttributes) {
        if(!courseService.belongs(userId, courseId) && !courseService.isPrivileged(userId, courseId))
            throw new UserNotFoundException();
        if (!errors.hasErrors()) {
            mailingService.sendEmail(authFacade.getCurrentUser(), userId, mailForm.getSubject(),
                    mailForm.getContent(), courseId);
            redirectAttributes.addFlashAttribute("successMessage","email.success.message");
            return new ModelAndView("redirect:/course/{courseId}/teachers");
        }
        return sendMail(courseId,userId,mailForm);
    }

}

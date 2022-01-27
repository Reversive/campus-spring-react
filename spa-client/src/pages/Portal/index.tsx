import React, { useEffect, useState } from "react";
import { SectionHeading } from "../../components/generalStyles/utils";
import { usePagination } from "../../hooks/usePagination";
import { Link } from "react-router-dom";
import AnnouncementUnit from "../../components/AnnouncementUnit";
import {
  CoursesContainer,
  Course,
  PortalAnnouncements,
  CourseName,
} from "./styles";
import BasicPagination from "../../components/BasicPagination";
import CourseModel from "../../../types/Course"

// i18next imports
import { useTranslation } from "react-i18next";
import "../../common/i18n/index";
import { CourseService } from "../../services/CoursesService";
import { Result } from "../../../types/Results";
//

function Portal() {
  const { t } = useTranslation();
  const [courses, setCourses] = useState(new Array(0));
  const [announcements, setAnnouncements] = useState(new Array(0));
  const maxPage = 3;
  const [currentPage, pageSize] = usePagination(10);

  var cs = new CourseService();
  var course = cs.getCourseById(12);
  useEffect(() => {
    setCourses([
      course.then(exito)
    ]);
    setAnnouncements([
      {
        announcementId: 1,
        title: "Hola",
        content:
          "xAAdddxAAdddxAAdddxAAdddxAAdddxAAdddxAdvxAAdddxAAdddxAAdddxAAdddAdddxAAdddxAAddd",
        author: { name: "juan", surname: "oriana" },
        date: "hoy",
      },
    ]);
  }, []);

  return (
    <>
      <SectionHeading>{t('Portal.title')}</SectionHeading>
      <CoursesContainer>
        {courses.map((course) => (
          // AGREGAR CHEQUEO DE SI ES CONTENIDO POR CURRENT COURESE O NO Y SI ES ESTUDIANTE O NO
          <Course isOld={false} key={course.courseId}>
            <CourseName style={{ display: "flex", alignItems: "center" }}>
              <Link to={`/course/${course.courseId}`}>
                {`${course.subject.name}[${course.board}]`}
              </Link>
              {/*<c:if test="${!coursesAsStudent.contains(courseItem)}">*/}
              {/*    <img src="<c:url value="/resources/images/graduation-hat.png"/>"*/}
              {/*         alt="<spring:message code=" img.alt.teacher.icon" />" style="margin-left: 10px"*/}
              {/*         width="28px"/>*/}
              {/*</c:if>*/}
            </CourseName>
            <p>
              {course.year} | {course.quarter}
            </p>
          </Course>
        ))}

        <BasicPagination
          currentPage={currentPage}
          pageSize={pageSize}
          maxPage={maxPage}
          baseURL={`/portal`}
          style={{ fontSize: "18px" }}
        />
      </CoursesContainer>

      <PortalAnnouncements>
        {announcements.length > 0 && (
          <>
            <SectionHeading>{t('Portal.lastAnnouncements')}</SectionHeading>
            {announcements.map((announcement) => (
              <AnnouncementUnit
                key={announcement.announcementId}
                course={{ courseId: 1 }}
                announcement={announcement}
                isGlobal={true}
              />
            ))}
          </>
        )}
      </PortalAnnouncements>
    </>
  );
}

export default Portal;

function exito(res:Result<CourseModel>):CourseModel{
  res.getData();

  console.log("GOLA rey");
  return res.getData();
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.AnnouncementDao;
import ar.edu.itba.paw.interfaces.AnnouncementService;
import ar.edu.itba.paw.models.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementDao announcementDao;

    @Override
    public Announcement create(Announcement announcement) {
        return announcementDao.create(announcement);
    }

    @Override
    public boolean update(long id, Announcement announcement) {
        return announcementDao.update(id, announcement);
    }

    @Override
    public boolean delete(long id) {
        return announcementDao.delete(id);
    }

    @Override
    public List<Announcement> list(long page, long pageSize) {
        return announcementDao.list(page, pageSize);
    }

    @Override
    public int getPageCount(long pageSize) {
        if(pageSize < 1) return 0;
        return announcementDao.getPageCount(pageSize);
    }

    @Override
    public List<Announcement> list(long page, long pageSize, Comparator<Announcement> comparator) {
        if(page < 1 || pageSize < 1) return new ArrayList<>();
        return list(page, pageSize).stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<Announcement> listByCourse(long courseId) {
        return announcementDao.listByCourse(courseId);
    }

    @Override
    public List<Announcement> listByCourse(long courseId, Comparator<Announcement> comparator) {
        return listByCourse(courseId).stream().sorted(comparator).collect(Collectors.toList());
    }


    @Override
    public Optional<Announcement> getById(long id) {
        return announcementDao.getById(id);
    }
}

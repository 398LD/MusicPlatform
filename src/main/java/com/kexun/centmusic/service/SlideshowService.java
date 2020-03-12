package com.kexun.centmusic.service;

import com.kexun.centmusic.data.SlideshowDao;
import com.kexun.centmusic.pojo.Slideshow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlideshowService {

    @Autowired
    private SlideshowDao slideshowDao;

    public List<Slideshow> list() {
        List<Slideshow> slideshows = slideshowDao.selectList(null);
        return slideshows;
    }

    public int add(Slideshow slideshow) {
        return slideshowDao.insert(slideshow);
    }

    public int del(int id) {
        return slideshowDao.deleteById(id);
    }

}

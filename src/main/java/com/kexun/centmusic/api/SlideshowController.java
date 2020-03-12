package com.kexun.centmusic.api;

import com.kexun.centmusic.pojo.Slideshow;
import com.kexun.centmusic.service.SlideshowService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("slideshow")
public class SlideshowController {
    @Value("${filePath}")
    private String filePath;
    private static Logger logger = Logger.getLogger(SlideshowController.class);
    @Autowired
    private SlideshowService slideshowService;

    @RequestMapping("list")
    public List<Slideshow> list() {
        return slideshowService.list();
    }

    @RequestMapping("uponload")
    public Map<String, Object> uponload(Slideshow slideshow, @RequestParam("file") MultipartFile multipartFile) {
        HashMap<String, Object> res = new HashMap<>();
        if (multipartFile.isEmpty()) {
            res.put("code", -1);
            res.put("msg", "文件不能为空");
            return res;
        } else {
            String originalFilename = multipartFile.getOriginalFilename();
            String hz = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
            String fileName = System.currentTimeMillis() + hz;
            slideshow.setTitle(fileName);
            saveFile(multipartFile);
            res.put("code", 1);
            res.put("msg", "上传成功");
            return res;
        }

    }

    public void saveFile(MultipartFile multipartFile) {
        File file = new File(filePath);
        try {
            multipartFile.transferTo(file);
        } catch (Exception e) {
            logger.info("文件保存时出现异常: " + e.getMessage());
        }

    }

}

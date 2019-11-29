package com.kexun.centmusic.api;

import com.kexun.centmusic.common.DateUtils;
import com.kexun.centmusic.platformdata.QMusic;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

@RestController
@RequestMapping("search")
public class SearchMusic {

    @Autowired
    private QMusic qMusic;
    private static Logger logger = Logger.getLogger(SearchMusic.class);

    @ResponseBody
    @RequestMapping(value = "music", method = RequestMethod.GET)
    public String music(String musicName, HttpServletRequest request) {
        logger.info("搜索请求 时间: " + DateUtils.getDateTime(new Date()) + "  文件名: " + musicName + " IP: " + getRealIp(request));
        return qMusic.srarchData(musicName);
    }

    @ResponseBody
    @RequestMapping("random")
    public String getRandom() {
        return qMusic.getRandom(6);
    }

    @ResponseBody
    @RequestMapping("top")
    public String getTop() {
        return qMusic.getTop100();
    }

    @ResponseBody
    @RequestMapping("playurl")
    public String getPlayUrl(String songmid) {
        return qMusic.getPlayUrl(songmid);
    }


    @ResponseBody
    @RequestMapping("getlrcArr")
    public String[] getlrcArr(int songid)  {
        return qMusic.getlrcArr(songid);
    }


    @RequestMapping(value = "download", method = RequestMethod.GET)
    public void download(String playurl, String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("下载请求 时间: " + DateUtils.getDate(new Date()) + " 文件名: " + fileName + " IP: " + getRealIp(request));
        response.setContentType("text/html;charset=utf-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".mp3");
            URL url = new URL(playurl);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            bis = new BufferedInputStream(inputStream);
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (bis != null)
                try {
                    bis.close();
                } catch (IOException e) {
                }
            if (bos != null)
                try {
                    bos.close();
                } catch (IOException e) {
                }
        }
    }

    public static String getRealIp(HttpServletRequest request) {
        // 这个一般是Nginx反向代理设置的参数
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多IP的情况（只取第一个IP）
        if (ip != null && ip.contains(",")) {
            String[] ipArray = ip.split(",");
            ip = ipArray[0];
        }
        return ip;
    }


}

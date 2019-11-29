package com.kexun.centmusic.platformdata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kexun.centmusic.common.DateUtils;
import com.kexun.centmusic.common.HttpUtils;
import com.kexun.centmusic.common.PinyinUtils;
import com.kexun.centmusic.common.RedisUtil;
import com.kexun.centmusic.config.PlatformURL;
import com.kexun.centmusic.pojo.Music;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class QMusic {

    @Autowired
    private RedisUtil redis;

    private static Logger logger = Logger.getLogger(QMusic.class);

    public String srarchData(String name) {
        String pinyin = PinyinUtils.ToPinyin(name);
        if (redis.hasKey(pinyin)) {
            long start = System.currentTimeMillis();
            String s = redis.get(pinyin);
            long end = System.currentTimeMillis();

            logger.info("走redis缓存 耗时: " + (end - start) + " 毫秒");

            return s;
        } else {
            long start = System.currentTimeMillis();
            String url = PlatformURL.QMUSIC + "?aggr=1&cr=1&flag_qc=0&p=1&n=60&w=" + name;
            String searchResult = HttpUtils.doGet(url);
            searchResult = searchResult.substring(searchResult.indexOf("(") + 1, searchResult.lastIndexOf(")"));
            ArrayList<Music> vos = getVo(searchResult);
            String s = JSON.toJSONString(vos);
            redis.set(pinyin, s);
            //保存22个小时
            redis.expire(pinyin, 22, TimeUnit.HOURS);
            long end = System.currentTimeMillis();
            logger.info("调用QQ音乐 耗时: " + (end - start) + " 毫秒");
            return s;
        }

    }

    //获取随机推荐的
    public String getRandom(int max) {
        String date = "random:" + DateUtils.getDate(new Date());
        if (redis.hasKey(date)) {
            return redis.get(date);
        } else {
            String result = HttpUtils.doGet(PlatformURL.ROMDOM);
            System.out.println(result);
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray songlist = jsonObject.getJSONArray("songlist");
            HashSet<Music> musics = new HashSet<Music>();
            while (musics.size() < max) {
                Random random = new Random();
                int i = random.nextInt(max);
                System.out.println(">>>>" + i);
                JSONObject song = (JSONObject) songlist.get(i);
                Music music = setModel(song.getJSONObject("data"));
                if (music == null) continue;
                musics.add(music);
            }
            String s = JSON.toJSONString(musics);
            redis.set(date, s);
            //缓存24小时
            redis.expire(date, 24, TimeUnit.HOURS);
            return s;
        }
    }

    public String getTop100() {
        String date = "top100:" + DateUtils.getDate(new Date());
        if (redis.hasKey(date)) {
            return redis.get(date);
        } else {
            String result = HttpUtils.doGet(PlatformURL.ROMDOM);
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray songlist = jsonObject.getJSONArray("songlist");
            List<Music> musics = new ArrayList<>();
            for (Object o : songlist) {
                JSONObject item = (JSONObject) o;
                JSONObject data = item.getJSONObject("data");
                Music music = setModel(data);
                musics.add(music);
            }
            String s = JSON.toJSONString(musics);
            redis.set(date, s);
            //缓存24小时
            redis.expire(date, 22, TimeUnit.HOURS);
            return s;
        }
    }


    //这里获取号我要的数据
    public ArrayList<Music> getVo(String data) {
        ArrayList<Music> maps = new ArrayList<>();
        JSONObject searchData = JSON.parseObject(data);
        JSONArray list = searchData.getJSONObject("data").getJSONObject("song").getJSONArray("list");
        for (Object o : list) {
            JSONObject item = (JSONObject) o;
            Music music = setModel(item);
            if (music == null) continue;
            maps.add(music);
        }
        return maps;
    }

    private Music setModel(JSONObject item) {
        Music music = new Music();
        String songname = item.getString("songname");
        String songmid = item.getString("songmid");
        Integer songid = item.getInteger("songid");
        Integer albumid = item.getInteger("albumid");
        music.setSongname(songname);
        music.setSinger(getSinger(item));
        music.setSongmid(songmid);
        music.setSongid(songid);
        music.setPhotourl(getPhoto(albumid));
        music.setCreateTime(System.currentTimeMillis());
        return music;
    }


    private String getPhoto(int albumid) {
        try {
            String photoUrl = "http://imgcache.qq.com/music/photo/album_300/%s/300_albumpic_%s_0.jpg";
            return String.format(photoUrl, albumid % 100, albumid);
        } catch (Exception e) {
            logger.error("专辑图片处理异常");
            return "";
        }
    }


    private String getSinger(JSONObject jsonObject) {
        try {
            JSONArray singers = jsonObject.getJSONArray("singer");
            StringBuffer sgStr = new StringBuffer();
            for (int i = 0; i < singers.size(); i++) {
                JSONObject sg = (JSONObject) singers.get(i);
                String name = sg.getString("name");
                sgStr.append(name);
                if (i < singers.size() - 1) {
                    sgStr.append("/");
                }
            }
            return sgStr.toString();
        } catch (Exception e) {
            return "";
        }


    }


    public String[] getlrcArr(int songid) {
        String lrcUrl = "http://music.qq.com/miniportal/static/lyric/%d/%d.xml";
        lrcUrl = String.format(lrcUrl, songid % 100, songid);
        String result = HttpUtils.doGet(lrcUrl, "GB2312");
        String substring = "";
        String[] split = null;
        try {
            substring = result.substring(result.indexOf("[[") + 1, result.lastIndexOf("]]"));
            split = substring.split("\n");
        } catch (Exception e) {
            split = new String[0];
        }
        return split;
    }


    public String getVkey(String songmid) {
        String token = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?format=json205361747&platform=yqq&cid=205361747&songmid=%s&filename=%s&guid=126548448";
        String formatToken = String.format(token, songmid, "C400" + songmid + ".m4a");
        String tokenResult = HttpUtils.doGet(formatToken);
        JSONObject tokenJson = JSON.parseObject(tokenResult);
        JSONArray jsonArray = tokenJson.getJSONObject("data").getJSONArray("items");
        if (jsonArray != null && jsonArray.size() > 0) {
            JSONObject it = (JSONObject) jsonArray.get(0);
            return it.getString("vkey");
        } else {
            return null;
        }
    }


    public String getPlayUrl(String songmid) {
        if (redis.hasKey("songmid:" + songmid)) {
            return redis.get("songmid:" + songmid);
        } else {
            String vkey = getVkey(songmid);
            if (StringUtils.isEmpty(vkey)) {
                logger.info("token获取失败");
                return "";
            }
            String qplayUrl = "http://ws.stream.qqmusic.qq.com/%s?fromtag=0&guid=126548448&vkey=%s";
            String format = String.format(qplayUrl, "C400" + songmid + ".m4a", vkey);
            System.out.println(format);
            redis.set("songmid:" + songmid, format);
            redis.expire("songmid:" + songmid, 22, TimeUnit.HOURS);
            return format;
        }
    }

    @Async
    public void saveData(String downloadUrl, String fileName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\music\\" + fileName + ".mp3"));
            URL url = new URL(downloadUrl);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            int length = 0;
            byte[] bytes = new byte[1024];
            while ((length = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, length);
            }
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("错误: " + e.getMessage());
        }
        logger.info("文件保存成功");

    }


}

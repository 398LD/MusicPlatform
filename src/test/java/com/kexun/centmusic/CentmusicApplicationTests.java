package com.kexun.centmusic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kexun.centmusic.platformdata.QMusic;
import com.kexun.centmusic.pojo.Music;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.List;

@SpringBootTest
class CentmusicApplicationTests {

    @Autowired
    private QMusic qMusic;
//
//    @Test
//    void contextLoads() throws UnsupportedEncodingException {
//        String[] s = qMusic.getlrcArr(245247351);
//        for (String s1 : s) {
//            System.out.println(s1);
//        }
//    }
//
//
//    @Test
//    void test2() {
//        String xus = qMusic.srarchData("祁隆");
//        System.out.println(xus);
//    }
//
//    @Test
//    void test3() {
//        String xus = qMusic.getRandom(6,2);
//        System.out.println(xus);
//    }


    @Test
    void test4() throws Exception {

        String top100 = qMusic.getTop100();
        JSONArray jsonArray = JSON.parseArray(top100);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String songmid = jsonObject.getString("songmid");
            String singer = jsonObject.getString("singer");
            int songid = jsonObject.getInteger("songid");
            String songname = jsonObject.getString("songname");
            String playUrl = qMusic.getPlayUrl(songmid);
            String fileName = songname + "-" + singer;

            if (StringUtils.isBlank(playUrl)) {
                System.out.println("无法获取播放地址" + fileName);
                continue;
            }
            qMusic.saveData(playUrl, fileName);
            System.out.println("音乐文件保存成功:" + fileName);
            String[] lrc = qMusic.getlrcArr(songid);
            if (lrc.length == 0) {
                System.out.println("歌词获取失败:" + fileName);
                continue;
            }
            saveLrc(fileName, lrc);
            System.out.println("歌词文件保存成功:" + fileName);
        }

    }

    public void saveLrc(String fileName, String[] lrc) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\music\\" + fileName + ".lrc"));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        for (int i = 0; i < lrc.length; i++) {

            bufferedWriter.write(lrc[i]);
            if (i < lrc.length - 1) {
                bufferedWriter.newLine();
            }

        }

        bufferedWriter.close();
        outputStreamWriter.close();
        fileOutputStream.close();
    }


    @Test
    public void testUrl() {
        String playUrl = qMusic.getPlayUrl("001xLIXo2w9V7U");
        System.out.println(playUrl);
    }


}

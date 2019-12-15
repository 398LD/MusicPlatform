package com.kexun.centmusic.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@TableName("music")
@Data
public class Music implements Serializable {

    private int id;
    private String songname;
    private String singer;
    private String photourl;
    @TableField("createTime")
    private long createTime;
    private int songid;
    private String songmid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return songid == music.songid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(songid);
    }
}

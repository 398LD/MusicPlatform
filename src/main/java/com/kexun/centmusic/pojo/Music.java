package com.kexun.centmusic.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@TableName("music")
@Data
@EqualsAndHashCode
public class Music implements Serializable {

    private int id;
    private String songname;
    private String singer;
    private String photourl;
    @TableField("createTime")
    private long createTime;
    private int songid;
    private String songmid;

}

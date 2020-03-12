package com.kexun.centmusic.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("slideshow")
public class Slideshow {
    @TableId(type = IdType.AUTO)
    private int id;
    @TableField("title")
    private String title;
    @TableField("url")
    private String url;
    @TableField("remark")
    private String remark;
    @TableField("filename")
    private String fileName;
}

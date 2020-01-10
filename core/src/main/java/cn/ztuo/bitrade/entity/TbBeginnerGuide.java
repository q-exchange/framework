package cn.ztuo.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/10/15 9:39
 * @Version: 1.0
 */
@Entity
@Table(name = "tb_beginner_guide", schema = "bitrade", catalog = "")
@ToString
public class TbBeginnerGuide {
    private long id;
    private String imgUrl;
    private String videoUrl;
    private String videoTitle;
    private String sort;
    private Timestamp createTime;
    private Timestamp updateTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "img_url", nullable = true, length = 1024)
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Basic
    @Column(name = "video_url", nullable = true, length = 1024)
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Basic
    @Column(name = "video_title", nullable = true, length = 50)
    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    @Basic
    @Column(name = "sort", nullable = true, length = 1)
    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Basic
    @Column(name = "create_time", nullable = true)
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @JsonIgnore
    @Column(name = "update_time", nullable = true)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbBeginnerGuide that = (TbBeginnerGuide) o;
        return id == that.id &&
                Objects.equals(imgUrl, that.imgUrl) &&
                Objects.equals(videoUrl, that.videoUrl) &&
                Objects.equals(videoTitle, that.videoTitle) &&
                Objects.equals(sort, that.sort) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, imgUrl, videoUrl, videoTitle, sort, createTime, updateTime);
    }
}

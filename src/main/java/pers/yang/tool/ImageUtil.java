package pers.yang.tool;

import cn.hutool.core.date.DateUtil;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.util.Date;

/**
 * 图片处理工具类
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-13 22:56:35
 */
public class ImageUtil {

    /**
     * 获取图像 EXIF 时间
     *
     * @param file 文件
     * @return {@link Date}
     */
    public static Date getImageExifTime(File file) {
        Metadata metadata;
        Date date = new Date();
        try {
            String timeStamp = "";
            metadata = JpegMetadataReader.readMetadata(file);
            for (Directory exif : metadata.getDirectories()) {
                for (Tag tag : exif.getTags()) {
                    if (tag.getTagName().equals("Date/Time Original")) {
                        timeStamp = tag.getDescription();
                    }
                }
            }
            date = DateUtil.parse(timeStamp, "yyyy:MM:dd HH:mm:ss");
        } catch (Exception e) {
            return date;
        }

        return date;
    }
}

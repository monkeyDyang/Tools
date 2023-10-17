package pers.yang.tool.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.crypto.SecureUtil;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.util.Date;
import java.util.Objects;

/**
 * 图片处理工具类
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-13 22:56:35
 */
public class ImageUtil {

    private ImageUtil() {
        throw new IllegalStateException("Utility class");
    }

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

    /**
     * 压缩图片
     *
     * @param imageFile 文件
     * @return {@link File}
     */
    public static File compress(File imageFile, File outFile) {
        ImgUtil.compress(imageFile, outFile, 0.5f);
        return outFile;
    }

    /**
     * 比较两个文件是否一致
     *
     * @param file1 第一个文件
     * @param file2 第二个文件
     * @return boolean
     */
    public static boolean compare(File file1, File file2) {
        return Objects.equals(SecureUtil.md5(file1), SecureUtil.md5(file2));
    }
}

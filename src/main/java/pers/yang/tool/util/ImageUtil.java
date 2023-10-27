package pers.yang.tool.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.StaticLog;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
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
     */
    public static void compress(File imageFile, File outFile) {
        ImgUtil.compress(imageFile, outFile, 0.5f);
    }

    /**
     * 复制文件的exif信息
     *
     * @param srcFile 源文件，exif信息的来源，压缩前的图片
     * @param outFile 目标文件，最后生成的文件
     */
    public static void saveExif(File srcFile, File outFile) {
        // 创建一个临时文件，复制压缩后的文件
        File tempFile = FileUtil.createTempFile();
        FileUtil.copy(outFile, tempFile, true);
        // 创建输出文件的输出流
        BufferedOutputStream outputStream = FileUtil.getOutputStream(outFile);
        OutputStream os = new BufferedOutputStream(outputStream);
        // 从压缩前的图片中获取exif信息，如果发生异常，则跳过后续操作
        try {
            // 获取exif信息
            TiffOutputSet outputSet = new TiffOutputSet();
            JpegImageMetadata srcMetadata = (JpegImageMetadata) Imaging.getMetadata(srcFile);
            List<TiffOutputDirectory> srcList = srcMetadata.getExif().getOutputSet().getDirectories();
            for (TiffOutputDirectory td : srcList) {
                outputSet.addDirectory(td);
            }
            // 读取源文件，替换exif信息，并将结果写入输出流
            new ExifRewriter().updateExifMetadataLossless(tempFile, os, outputSet);
        } catch (ImageReadException | IOException | ImageWriteException e) {
            StaticLog.warn("File: {} have no exif.", srcFile.getName());
        }
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

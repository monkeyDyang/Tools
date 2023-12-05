package pers.yang.tool.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.log.StaticLog;
import pers.yang.tool.util.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

/**
 * 重命名服务的抽象类
 * <p>
 * - 实现重命名服务的公共方法
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-10 21:43:12
 */
public abstract class AbstractPhotoHandleService implements PhotoHandleService {

    @Override
    public void handlePhoto(File srcFile, boolean isCompress) {
        try {
            // 获取新文件
            File newFile = getNewFile(srcFile);

            // 压缩文件
            if (isCompress) {
                ImageUtil.compress(srcFile, newFile);
            } else {
                FileUtil.copy(srcFile, newFile, true);
            }

            // 保留exif信息
            ImageUtil.saveExif(srcFile, newFile);

            // 处理源文件
            handleSrcFile(srcFile);
        } catch (Exception e) {
            StaticLog.error(e);
        }

    }

    /**
     * 获取最终时间
     *
     * @param file 文件
     * @return {@link Date}
     */
    protected Date getRightTime(File file) {
        // 从文件名获取时间
        Date fileNameTime = handlerFileName(file.getName());
        // 从exif信息中获取时间
        Date exifTime = ImageUtil.getImageExifTime(file);

        return exifTime.before(fileNameTime) ? exifTime : fileNameTime;
    }

    /**
     * 创建新的文件
     *
     * @param srcFile 源文件
     * @return {@link File} 新文件
     */
    protected File getNewFile(File srcFile) {
        // 获取真实的拍摄时间
        Date rightTime = getRightTime(srcFile);

        // 获取年份
        int year = DateUtil.year(rightTime);
        // 获取月份
        int month = DateUtil.month(rightTime) + 1;
        String dictionary = year + "年-" + month + "月";
        // 获取文件名
        String newFileName = DateUtil.format(rightTime, "yyyy-MM-dd_HH-mm-ss") + "." + FileUtil.getSuffix(srcFile);
        // 文件全路径
        String newFilePath = srcFile.getParentFile() + File.separator + dictionary + File.separator + newFileName;
        File newFile = FileUtil.file(newFilePath);
        if (!newFile.getParentFile().exists() && (!newFile.getParentFile().mkdirs())) {
            StaticLog.error("Dictionary： /{} create fail.", dictionary);
        }
        // 处理重复文件
        handleRepeatPhoto(srcFile, newFile);
        return newFile;
    }

    /**
     * 处理重复照片
     * <p>
     * 将重复的文件移动到指定的目录下
     *
     * @param srcFile 源文件
     * @param newFile 新文件
     */
    protected void handleRepeatPhoto(File srcFile, File newFile) {
        //  文件已存在，并且是重复文件
        if (newFile.exists() && ImageUtil.compare(srcFile, newFile)) {
            // 移动重复文件到指定目录
            String path = srcFile.getParentFile() + File.separator + "Repeat" + File.separator + srcFile.getName();
            File repeatFile = new File(path);
            if (!repeatFile.getParentFile().exists() && (!repeatFile.getParentFile().mkdirs())) {
                StaticLog.error("Dictionary /Repeat create fail.");
            }
            if (!srcFile.renameTo(repeatFile)) {
                StaticLog.error("Rename fail.");
            }
            StaticLog.warn("{} exists, skip it.", newFile.getName());
            throw new RuntimeException("Repeat photo, skip it.");
        }
    }

    /**
     * 处理源文件
     *
     * @param srcFile 文件
     */
    protected void handleSrcFile(File srcFile) {
        try {
            Files.deleteIfExists(srcFile.toPath());
        } catch (IOException e) {
            StaticLog.error("File {} Delete fail.", srcFile.getName(), e);
        }
    }
}

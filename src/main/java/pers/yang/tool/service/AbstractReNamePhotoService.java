package pers.yang.tool.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.log.StaticLog;
import pers.yang.tool.ImageUtil;

import java.io.File;
import java.util.Date;

/**
 * Description:
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-10 21:43:12
 */
public abstract class AbstractReNamePhotoService implements ReNamePhotoService {

    @Override
    public void handlePhoto(File file) {
        // 获取真实的拍摄时间
        Date rightTime = getRightTime(file);
        // 移动并重命名文件
        moveAndReNamePhoto(file, rightTime);
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
     * 移动和重新命名照片
     *
     * @param file      文件
     * @param rightTime 真实时间
     */
    protected void moveAndReNamePhoto(File file, Date rightTime) {
        // 获取年份
        int year = DateUtil.year(rightTime);
        // 获取月份
        int month = DateUtil.month(rightTime) + 1;
        String dictionary = year + "年-" + month + "月";
        // 获取最终的日期时间
        String newFileName = DateUtil.format(rightTime, "yyyy-MM-dd_HH-mm-ss") + "." + FileUtil.getSuffix(file);

        // 重命名文件并移动到对应月份文件夹下
        String newFilePath = file.getParentFile() + File.separator + dictionary + File.separator + newFileName;

        File newFile = FileUtil.file(newFilePath);
        if (!newFile.getParentFile().exists() && (!newFile.getParentFile().mkdirs())) {
            StaticLog.error("Dictionary： /{} create fail.", dictionary);
        }

        //  确保新的文件名不存在
        if (newFile.exists()) {
            StaticLog.error("{} exists, skip it.", newFileName);
            // 移动重复文件到指定目录
            String path = file.getParentFile() + File.separator + "Repeat" + File.separator + file.getName();
            File repeatFile = new File(path);
            if (!repeatFile.getParentFile().exists() && (!repeatFile.getParentFile().mkdirs())) {
                StaticLog.error("Dictionary /Repeat create fail.");
            }
            if (!file.renameTo(repeatFile)) {
                StaticLog.error("Rename fail.");
            }
            return;
        }
        FileUtil.rename(file, newFileName, true);
    }
}

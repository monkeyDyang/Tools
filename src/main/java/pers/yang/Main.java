package pers.yang;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.log.StaticLog;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-01 11:47:08
 */
public class Main {
    /**
     * 基本路径
     */
    private static final String BASE_PATH = "D:\\本地资源库\\果果\\照片";
//    private static final String BASE_PATH = "D:\\Files\\TestImage";

    private static List<File> allFileList = new ArrayList<>();

    /**
     * 微信前缀
     */
    public static final String WECHAT_PREFIX = "mmexport";

    /**
     * 小米前缀
     */
    public static final String XIAOMI_PREFIX = "IMG";
    /**
     * 微信相机前缀
     */
    public static final String WECHAT_CAMERA_PREFIX = "wx_camera_";


    public static void main(String[] args) {
        // 获取所有文件
        getFiles();
        for (File file : allFileList) {
            // 处理微信图片
            if (file.getName().startsWith(WECHAT_PREFIX)) {
                handleWeChatPhoto(file);
            }
            // 处理微信相机拍摄的图片
            if (file.getName().startsWith(WECHAT_CAMERA_PREFIX)) {
                handleWeChatCameraPhoto(file);
            }
            // 处理小米图片
            if (file.getName().startsWith(XIAOMI_PREFIX)) {
                handleCameraPhoto(file);
            }
            // 截图移动到指定文件夹下
            if (file.getName().startsWith("Screenshot")) {
                String path = BASE_PATH + File.separator + "Screenshot" + File.separator + file.getName();
                File screenshot = new File(path);
                if (!screenshot.getParentFile().exists() && (!screenshot.getParentFile().mkdirs())) {
                    StaticLog.error("Dictionary /Screenshot create fail.");
                }
                if (!file.renameTo(screenshot)) {
                    StaticLog.error("Rename fail.");
                }
            }
        }
    }

    /**
     * 获取指定目录以及子目录下的所有文件
     */
    private static void getFiles() {
        File dir = new File(BASE_PATH);
        if (!dir.exists()) {
            System.out.println("目录不存在");
        }
        getAllFile(dir, allFileList);
        System.out.println("该文件夹下共有" + allFileList.size() + "个文件");
    }

    /**
     * 递归获取子目录
     *
     * @param fileInput   文件地址
     * @param allFileList 文件列表
     */
    private static void getAllFile(File fileInput, List<File> allFileList) {
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                getAllFile(file, allFileList);
            } else {
                allFileList.add(file);
            }
        }
    }

    /**
     * 处理相机照片
     *
     * @param file 文件
     */
    private static void handleCameraPhoto(File file) {

        String fileName = file.getName();
        // 去掉下划线
        fileName = fileName.replace("_", "");
        // 获取文件名中的时间
        String timeString = fileName.replace(XIAOMI_PREFIX, "");
        String suffix = timeString.substring(timeString.lastIndexOf("."));
        // 有的照片会多一个 01
        timeString = timeString.replace(suffix, "").substring(0, 14);
        DateTime dateTime;
        try {
            dateTime = DateUtil.parse(timeString, "yyyyMMddHHmmss");
        } catch (Exception e) {
            StaticLog.warn("Skip file: {}", fileName);
            return;
        }

        // 获取图片信息中的时间信息
        Date exifTime = getImageExifTime(file);

        // 获取最终时间
        Date realTime = getRealTime(exifTime, dateTime);

        moveAndRenameFile(file, realTime, suffix);
    }

    /**
     * 处理微信拍摄的照片名字 mmexport时间戳
     *
     * @param file 文件
     */
    private static void handleWeChatPhoto(File file) {
        // 获取图片名字中的时间信息
        String name = file.getName();
        // 去除前缀
        String timeStampString = name.replace(WECHAT_PREFIX, "");
        // 去除后缀
        String suffix = timeStampString.substring(timeStampString.lastIndexOf("."));
        // 取十三位的时间戳，有的图片会存在(1)
        timeStampString = timeStampString.replace(suffix, "").substring(0, 13);
        long timeStampLong;
        try {
            timeStampLong = Long.parseLong(timeStampString);
        } catch (Exception ex) {
            StaticLog.warn("Skip file: {}", name);
            return;
        }
        Date fileNameTime = new Date(timeStampLong);

        // 获取图片信息中的时间信息
        Date exifTime = getImageExifTime(file);

        // 获取最终时间
        Date realTime = getRealTime(exifTime, fileNameTime);

        moveAndRenameFile(file, realTime, suffix);
    }

    /**
     * 处理微信相机照片
     *
     * @param file 文件
     */
    private static void handleWeChatCameraPhoto(File file) {
        // 获取图片名字中的时间信息
        String name = file.getName();
        // 去除前缀
        String timeStampString = name.replace(WECHAT_CAMERA_PREFIX, "");
        // 去除后缀
        String suffix = timeStampString.substring(timeStampString.lastIndexOf("."));
        timeStampString = timeStampString.replace(suffix, "");
        long timeStampLong;
        try {
            timeStampLong = Long.parseLong(timeStampString);
        } catch (Exception ex) {
            StaticLog.warn("Skip file: {}", name);
            return;
        }
        Date fileNameTime = new Date(timeStampLong);

        // 获取图片信息中的时间信息
        Date exifTime = getImageExifTime(file);

        // 获取最终时间
        Date realTime = getRealTime(exifTime, fileNameTime);

        moveAndRenameFile(file, realTime, suffix);
    }

    /**
     * 移动和重命名文件
     *
     * @param file     文件
     * @param realTime 实时
     * @param suffix   后缀
     */
    private static void moveAndRenameFile(File file, Date realTime, String suffix) {
        // 获取年份
        int year = DateUtil.year(realTime);
        // 获取月份
        int month = DateUtil.month(realTime) + 1;

        String dictionary = year + "年-" + month + "月";

        // 获取最终的日期时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateString = sdf.format(realTime);

        // 重命名文件并移动到对应月份文件夹下
        String newFileName = BASE_PATH + File.separator + dictionary + File.separator + dateString + suffix;
        File newFile = new File(newFileName);
        if (!newFile.getParentFile().exists() && (!newFile.getParentFile().mkdirs())) {
            StaticLog.error("Dictionary： /{} create fail.", dictionary);
        }

        //  确保新的文件名不存在
        if (newFile.exists()) {
            StaticLog.error("{} exists, skip it.", newFileName);
            // 移动重复文件到指定目录
            String path = BASE_PATH + File.separator + "Repeat" + File.separator + file.getName();
            File repeatFile = new File(path);
            if (!repeatFile.getParentFile().exists() && (!repeatFile.getParentFile().mkdirs())) {
                StaticLog.error("Dictionary /Repeat create fail.");
            }
            if (!file.renameTo(repeatFile)) {
                StaticLog.error("Rename fail.");
            }
            return;
        }
        if (!file.renameTo(newFile)) {
            StaticLog.error("Rename fail.");
        }
    }

    /**
     * 获取真正的时间
     *
     * @param exifTime     Exif 时间
     * @param fileNameTime 文件名时间
     * @return {@link Date}
     */
    private static Date getRealTime(Date exifTime, Date fileNameTime) {
        Date realTime;
        // 两个时间中选择更早的那一个时间
        if (exifTime.before(fileNameTime)) {
            realTime = exifTime;
        } else {
            realTime = fileNameTime;
        }
        return realTime;
    }

    /**
     * 获取图像 EXIF 时间
     *
     * @param jpegFile JPEG 文件
     * @return {@link Date}
     */
    private static Date getImageExifTime(File jpegFile) {
        Metadata metadata;
        Date date = new Date();
        try {
            String timeStamp = "";
            metadata = JpegMetadataReader.readMetadata(jpegFile);
            for (Directory exif : metadata.getDirectories()) {
                for (Tag tag : exif.getTags()) {
                    if (tag.getTagName().equals("Date/Time Original")) {
                        timeStamp = tag.getDescription();
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            date = sdf.parse(timeStamp);
        } catch (Exception e) {
            return date;
        }

        return date;
    }
}
package pers.yang.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.StaticLog;
import pers.yang.tool.service.PhotoHandleService;
import pers.yang.tool.service.impl.PhoneCameraHandleServiceImpl;
import pers.yang.tool.service.impl.WeChatPhotoHandleServiceImpl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<String, PhotoHandleService> photoServiceMap = new HashMap<>();

    static {
        photoServiceMap.put(WECHAT_PREFIX, new WeChatPhotoHandleServiceImpl());
        photoServiceMap.put(XIAOMI_PREFIX, new PhoneCameraHandleServiceImpl());
        photoServiceMap.put(WECHAT_CAMERA_PREFIX, new PhoneCameraHandleServiceImpl());
    }

    public static void main(String[] args) {
        // 获取所有文件
        List<File> allFileList = FileUtil.loopFiles(BASE_PATH);
        for (File file : allFileList) {
            // 处理微信图片
            if (file.getName().startsWith(WECHAT_PREFIX)) {
                photoServiceMap.get(WECHAT_PREFIX).handlePhoto(file);
            }
            // 处理微信相机拍摄的图片
            if (file.getName().startsWith(WECHAT_CAMERA_PREFIX)) {
                photoServiceMap.get(WECHAT_CAMERA_PREFIX).handlePhoto(file);
            }
            // 处理小米图片
            if (file.getName().startsWith(XIAOMI_PREFIX)) {
                photoServiceMap.get(XIAOMI_PREFIX).handlePhoto(file);
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
}
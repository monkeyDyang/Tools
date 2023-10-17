package pers.yang.tool.service.impl;

import cn.hutool.log.StaticLog;
import pers.yang.tool.service.AbstractPhotoHandleService;
import pers.yang.tool.service.PhotoHandleService;

import java.util.Date;

/**
 * Description:
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-10 21:37:40
 */
public class WeChatCameraHandleServiceImpl extends AbstractPhotoHandleService implements PhotoHandleService {

    @Override
    public Date handlerFileName(String fileName) {
        // 去除前缀
        String timeStampString = fileName.replace("wx_camera_", "");
        // 去除后缀
        String suffix = timeStampString.substring(timeStampString.lastIndexOf("."));
        timeStampString = timeStampString.replace(suffix, "");
        long timeStampLong;
        try {
            timeStampLong = Long.parseLong(timeStampString);
        } catch (Exception ex) {
            StaticLog.warn("Skip file: {}", fileName);
            return null;
        }
        return new Date(timeStampLong);
    }
}

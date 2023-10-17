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
 * @date 2023-10-10 21:36:41
 */
public class WeChatPhotoHandleServiceImpl extends AbstractPhotoHandleService implements PhotoHandleService {

    @Override
    public Date handlerFileName(String fileName) {
        // 去除前缀
        String timeStampString = fileName.replace("mmexport", "");
        // 去除后缀
        String suffix = timeStampString.substring(timeStampString.lastIndexOf("."));
        // 取十三位的时间戳，有的图片会存在(1)
        timeStampString = timeStampString.replace(suffix, "").substring(0, 13);
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

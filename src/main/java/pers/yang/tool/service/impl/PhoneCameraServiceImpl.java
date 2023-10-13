package pers.yang.tool.service.impl;

import cn.hutool.core.date.DateUtil;
import pers.yang.tool.service.AbstractReNamePhotoService;
import pers.yang.tool.service.ReNamePhotoService;

import java.util.Date;

/**
 * Description:
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-10 21:38:18
 */
public class PhoneCameraServiceImpl extends AbstractReNamePhotoService implements ReNamePhotoService {

    @Override
    public Date handlerFileName(String fileName) {
        // 去掉下划线
        fileName = fileName.replace("_", "");
        // 获取文件名中的时间
        String timeString = fileName.replace("IMG", "");
        String suffix = timeString.substring(timeString.lastIndexOf("."));
        // 有的照片会多一个 01
        timeString = timeString.replace(suffix, "").substring(0, 14);
        return DateUtil.parse(timeString, "yyyyMMddHHmmss");
    }
}

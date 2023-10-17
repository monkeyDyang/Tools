package pers.yang.tool.service;

import java.io.File;
import java.util.Date;

/**
 * 图片处理服务
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-10 20:01:57
 */
public interface PhotoHandleService {

    /**
     * 处理照片
     * <p>
     * 1. 获取真实拍摄时间
     * 2. 文件重命名
     * 3. 移动到所属月份的文件夹下
     *
     * @param file 文件
     */
    void handlePhoto(File file);

    /**
     * 从文件名中提取拍摄时间
     *
     * @param fileName 文件名
     * @return {@link Date}
     */
    Date handlerFileName(String fileName);
}

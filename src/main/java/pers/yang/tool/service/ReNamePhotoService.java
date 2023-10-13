package pers.yang.tool.service;

import java.io.File;
import java.util.Date;

/**
 * Description:
 *
 * @author YangYang
 * @version 1.0.0
 * @date 2023-10-10 20:01:57
 */
public interface ReNamePhotoService {

    /**
     * 处理照片
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

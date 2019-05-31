package com.liser.oauth2.authorization;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 基于位运算的授权处理器
 *
 * @author LISER
 * @date 2019/5/12
 */
@Slf4j
public class AuthorizationProcessor {

    /**
     * 权限校验
     * 计算公式：list.get(position) & value > 0 ? 有权限:无权限
     *
     * @param existingSets 待验证集
     * @param indexes      第几个Long型空间
     * @param position     Long型对应的二进制数中所处的位置
     * @return 校验结果
     */
    public static boolean checkOperation(List<Integer> existingSets, Integer indexes, Integer position) {
        if (existingSets.size() < indexes) {
            return false;
        }
        double pow = Math.pow(2, position);
        int result = existingSets.get(indexes) & (int) pow;
        return result > 0;
    }

    /**
     * 或运算实现添加操作
     *
     * @param existingSets 待验证集
     * @param indexes      第几个Long型空间
     * @param position     Long型对应的二进制数中所处的位置
     */
    public static void addOperation(List<Integer> existingSets, Integer indexes, Integer position) {
        if (existingSets.size() < indexes) {
            return;
        }
        double pow = Math.pow(2, position);
        int result = existingSets.get(indexes) | (int) pow;
        existingSets.set(position, result);
    }

    /**
     * 非运算实现移除操作
     *
     * @param existingSets 待验证集
     * @param indexes      第几个Long型空间
     * @param position     Long型对应的二进制数中所处的位置
     */
    public static void removeOperation(List<Integer> existingSets, Integer indexes, Integer position) {
        if (existingSets.size() < indexes) {
            return;
        }
        double pow = Math.pow(2, position);
        int result = existingSets.get(indexes) ^ (int) pow;
        existingSets.set(position, result);
    }
}

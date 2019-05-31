package com.liser.leaf.api;

/**
 * @author LISER
 * @date 2019/5/12
 */
public interface SequenceService {

    /**
     * 获取序列
     *
     * @param businessName 业务名称
     * @return
     */
    Long getSequence(String businessName);

}

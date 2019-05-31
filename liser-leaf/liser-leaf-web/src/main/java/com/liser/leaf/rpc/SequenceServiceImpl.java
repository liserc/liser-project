package com.liser.leaf.rpc;

import com.liser.leaf.api.SequenceService;
import com.liser.leaf.service.SegmentService;
import com.sankuai.inf.leaf.common.Result;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author LISER
 * @date 2019/5/12
 */
@Service
public class SequenceServiceImpl implements SequenceService {

    @Autowired
    SegmentService segmentService;

    @Override
    public Long getSequence(String businessName) {
        Result result = segmentService.getId(businessName);
        return result.getId();
    }
}

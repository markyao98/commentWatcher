package publicInterface;

import com.markyao.model.dto.RestData;

public interface RpcAnalyzerSerivce {
    RestData analyzerByAid(String[] aids);
}

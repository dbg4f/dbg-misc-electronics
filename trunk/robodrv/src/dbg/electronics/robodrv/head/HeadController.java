package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.Range;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created: 1/28/13  9:30 PM
 */
public class HeadController implements ParameterUpdater {

    private Map<MasterParameterType, MasterParameter> actualParameters = new LinkedHashMap<MasterParameterType, MasterParameter>();

    private Map<MasterParameterType, MasterParameter> targetParameters = new LinkedHashMap<MasterParameterType, MasterParameter>();

    public Range getParameterRange(MasterParameterType type) {
        return checkedGet(type, actualParameters).getRange();
    }

    private MasterParameter checkedGet(MasterParameterType type, Map<MasterParameterType, MasterParameter> parametersMap) {
        if (!parametersMap.containsKey(type)) {
            throw new IllegalArgumentException("Master parameter type not found " + type + " in " + parametersMap.keySet());
        }
        return parametersMap.get(type);
    }

    public MasterParameter getCurrentActualParameter(MasterParameterType type) {
        return checkedGet(type, actualParameters).snapshot();
    }

    public MasterParameter getCurrentTargetParameter(MasterParameterType type) {
        return checkedGet(type, targetParameters).snapshot();
    }

    @Override
    public void update(MasterParameterType type, int value) {
        checkedGet(type, targetParameters).update(value);
    }
}

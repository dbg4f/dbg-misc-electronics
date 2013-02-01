package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.Range;

public class TargetParameterAccessor extends MasterParameterAccessor {
    @Override
    public MasterParameter getMasterParameter() {
        return headController.getCurrentTargetParameter(type);
    }
}

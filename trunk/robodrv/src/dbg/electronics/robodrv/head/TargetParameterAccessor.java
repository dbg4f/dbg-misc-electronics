package dbg.electronics.robodrv.head;


public class TargetParameterAccessor extends MasterParameterAccessor {
    @Override
    public MasterParameter getMasterParameter() {
        return headController.getCurrentTargetParameter(type);
    }
}

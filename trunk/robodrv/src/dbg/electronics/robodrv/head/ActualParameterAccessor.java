package dbg.electronics.robodrv.head;

public class ActualParameterAccessor extends MasterParameterAccessor {
    @Override
    public MasterParameter getMasterParameter() {
        return headController.getCurrentActualParameter(type);
    }
}

package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.Range;

public abstract class MasterParameterAccessor implements ParameterAccessor {

    protected HeadController headController;

    protected MasterParameterType type;

    public void setHeadController(HeadController headController) {
        this.headController = headController;
    }

    public void setType(MasterParameterType type) {
        this.type = type;
    }

    @Override
    public int getValue() {
        return getMasterParameter().getValue();
    }

    @Override
    public Range getRange() {
        return getMasterParameter().getRange();
    }

    public abstract MasterParameter getMasterParameter();

}

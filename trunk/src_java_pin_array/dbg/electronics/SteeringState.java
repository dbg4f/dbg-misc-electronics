package dbg.electronics;

/**
* Created by IntelliJ IDEA.
* User: dmitry
* Date: 1/4/12
* Time: 10:14 PM
* To change this template use File | Settings | File Templates.
*/
class SteeringState {

    long ts;

    int pos;

    int ticks;

    SteeringState(int pos) {

        this.pos = pos;

        ts = System.currentTimeMillis();

        ticks = McUtils.range(pos, 0x00, 0xFF, -12, 12, false);

    }



}

package dbg.electronics.robodrv.mcu;

import java.io.IOException;

import static dbg.electronics.robodrv.mcu.CommandCode.READ_REG;
import static dbg.electronics.robodrv.mcu.CommandCode.WRITE_REG;
import static dbg.electronics.robodrv.mcu.McuCommand.createCommand;

public class McuRegisterAccess {

    final SynchronousExecutor executor;

    public McuRegisterAccess(SynchronousExecutor executor) {
        this.executor = executor;
    }

    public void writeReg(M16Reg reg, int value) throws InterruptedException, McuCommunicationException, IOException {

        CommandResponse response = executor.execute(createCommand(WRITE_REG, reg.toCode(), value));

    }


    public int readReg(M16Reg reg) throws InterruptedException, McuCommunicationException, IOException {

        CommandResponse response = executor.execute(createCommand(READ_REG, reg.toCode()));

        return response.getResult();

    }


}


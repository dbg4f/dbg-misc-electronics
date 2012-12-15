package dbg.electronics;

import static dbg.electronics.McCommandType.*;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 12/13/11
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */
public enum McCommand {

    ECHO(0x01),
    SET_PWM0(0x04),
    SET_PWM1(0x05),
    GET_INT_COUNTER(0x10),
    SCHEDULE_PWM1(0x11),
    CANCEL_SCHEDULE_PWM1(0x12),
    SET_FINAL_PWM1(0x16, NO_RESPONSE),
    READ_REG(0x14),
    WRITE_REG(0x15),

    CMD_L1_ECHO 		(0x01),
    CMD_L1_READ_REG 	(0x14),
    CMD_L1_WRITE_REG 	(0x15),
    CMD_L1_READ_ADC0 	(0x16),
    CMD_L1_ENABLE_ADC 	(0x17);


    private McCommand(int code) {
        this.code = (byte)code;
        this.commandType = McCommandType.DEFINED_RESPONSE;
    }

    private McCommand(int code, McCommandType commandType) {
        this.code = (byte)code;
        this.commandType = commandType;
    }

    private byte code;
    private McCommandType commandType;

    public byte getCode() {
        return code;
    }

    public McCommandType getCommandType() {
        return commandType;
    }
}

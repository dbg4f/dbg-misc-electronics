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

    ECHO(0x01, MIRROR_PARAM),
    SET_PWM0(0x04, 0x14),
    SET_PWM1(0x05, 0x15),
    GET_INT_COUNTER(0x10, 0x15),
    SCHEDULE_PWM1(0x11),
    CANCEL_SCHEDULE_PWM1(0x12),
    SET_FINAL_PWM1(0x16, NO_RESPONSE),
    READ_REG(0x14, 0xDD),
    WRITE_REG(0x15, 0xDA);

    private McCommand(int code) {
        this.code = (byte)code;
        this.commandType = McCommandType.MIRROR_CMD;
    }

    private McCommand(int code, int confirmOkCode) {
        this.code = (byte)code;
        this.commandType = McCommandType.DEFINED_RESPONSE;
        this.confirmOkCode = (byte)confirmOkCode;
    }

    private McCommand(int code, McCommandType commandType) {
        this.code = (byte)code;
        this.commandType = commandType;
    }

    private byte code;
    private byte confirmOkCode = -1;
    private McCommandType commandType;

    public byte getCode() {
        return code;
    }

    public byte getConfirmOkCode() {
        return confirmOkCode;
    }

    public McCommandType getCommandType() {
        return commandType;
    }
}

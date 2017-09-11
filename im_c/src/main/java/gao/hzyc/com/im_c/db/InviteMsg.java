package gao.hzyc.com.im_c.db;

/**
 * 邀请信息的实体类
 * Created by codeforce on 2017/5/6.
 */
public class InviteMsg {

    //id
    private int id;
    //来源
    private String from;
    //发送时间
    private long time;
    //邀请理由
    private String reason;
    //消息状态
    private MsgState state;
    public enum MsgState{
        /**被邀请*/
        BEINVITEED,
        /**被拒绝*/
        BEREFUSED,
        /**对方同意*/
        BEAGREED,
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public MsgState getState() {
        return state;
    }

    public void setState(MsgState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "InviteMsg{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", time=" + time +
                ", reason='" + reason + '\'' +
                ", state=" + state +
                '}';
    }
}

package grocket.com.smart119citizen.data;

/**
 * 지령 데이터
 */
public class CommandItemData {

    /**
     * 지령 PK
     */
    private String Primarykey = "";

    /**
     * 지령 종류
     0 - “화재출동"
     1 - “구조출동"
     2 - “구급출동"
     */
    private int CommandType;

    /**
     * 재해번호
     */
    private String AccidentNo = "";

    /**
     * 사고 위치 주소 (지번 주소)
     */
    private String AccidentAddress = "";

    /**
     * 상세 주소
     */
    private String DetailAddress = "";

    /**
     * 사고 내용
     */
    private String AccidentContent = "";

    /**
     * 신고자 전화번호
     */
    private String ReporterTelephone = "";

    private String RegDate = "";

    public String getPrimarykey() {
        return Primarykey;
    }

    public void setPrimarykey(String primarykey) {
        Primarykey = primarykey;
    }

    public int getCommandType() {
        return CommandType;
    }

    public void setCommandType(int commandType) {
        CommandType = commandType;
    }

    public String getAccidentNo() {
        return AccidentNo;
    }

    public void setAccidentNo(String accidentNo) {
        AccidentNo = accidentNo;
    }

    public String getAccidentAddress() {
        return AccidentAddress;
    }

    public void setAccidentAddress(String accidentAddress) {
        AccidentAddress = accidentAddress;
    }

    public String getDetailAddress() {
        return DetailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        DetailAddress = detailAddress;
    }

    public String getAccidentContent() {
        return AccidentContent;
    }

    public void setAccidentContent(String accidentContent) {
        AccidentContent = accidentContent;
    }

    public String getReporterTelephone() {
        return ReporterTelephone;
    }

    public void setReporterTelephone(String reporterTelephone) {
        ReporterTelephone = reporterTelephone;
    }

    public String getRegDate() {
        return RegDate;
    }

    public void setRegDate(String regDate) {
        RegDate = regDate;
    }
}

package grocket.com.smart119citizen.data;

/**
 * 사용자 지정번호 데이터
 */
public class ContactItemData {
    private String PrimaryKey = "";
    private String Name = "";
    private String Position = "";
    private String Telephone = "";

    public String getPrimaryKey() {
        return PrimaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        PrimaryKey = primaryKey;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }
}

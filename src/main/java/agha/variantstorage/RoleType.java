package agha.variantstorage;

public enum RoleType{

    ADMIN("ROLE_ADMIN", "Admin");

    private String value;
    private String displayName;

    private RoleType(String value,String displayName){
        this.value=value;
        this.displayName=displayName;
    }

    public String getValue() {
        return value;
        }

    public String getDisplayName() {
        return displayName;
    }

    public String toString() {
        return value;
    }

}
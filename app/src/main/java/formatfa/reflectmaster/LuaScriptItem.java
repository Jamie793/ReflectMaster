package formatfa.reflectmaster;

public class LuaScriptItem {
    private String path;
    private byte[] data;

    public LuaScriptItem(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

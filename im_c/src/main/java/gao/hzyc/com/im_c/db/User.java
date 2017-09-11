package gao.hzyc.com.im_c.db;

/**
 * User的实体类
 * Created by codeforce on 2017/5/6.
 */
public class User {

    //姓名
    private String name;
    //头像
    private String avast;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvast() {
        return avast;
    }

    public void setAvast(String avast) {
        this.avast = avast;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", avast='" + avast + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return name == ((User) o).getName();

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (avast != null ? avast.hashCode() : 0);
        return result;
    }
}

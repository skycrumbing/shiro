package com.how2java.dao;

import com.how2java.bean.User;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/9/17 0017.
 */
public class DAO {
    public DAO(){
        //Class.forName是把这个类加载到JVM中，加载的时候，就会执行其中的静态初始化块，完成驱动的初始化的相关工作。
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println("驱动加载失败");
        }
    }

    public Connection getConnect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/shiro?characterEncoding=UTF-8","root","admin");
    }

    public String getPassWord(String userName){
        String sql="select password from user where name = ?";
        try(Connection conn = getConnect(); PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password");
            }
        }catch(SQLException e){
                e.printStackTrace();
        }
        return null;
    }

    public Set<String> listRole(String userName){
        Set<String> roles = new HashSet<>();
        String sql = "select r.name from user u "
                + "left join user_role ur on u.id = ur.uid "
                + "left join Role r on r.id = ur.rid "
                + "where u.name = ?";
        try(Connection conn = getConnect();PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1,userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                roles.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public Set<String> listPermit(String userName){
        Set<String> permits = new HashSet<>();
        String sql = "select p.name from user u "
                + "left join user_role ur on ur.uid = u.id "
                +  "left join role r on r.id = ur.rid "
                + "left join role_permission rp on r.id = rp.rid "
                + "left join permission p on p.id = rp.pid "
                + "where u.name = ?";
        try(Connection conn = getConnect(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1,userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                permits.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permits;
    }

    public String creatUser(String userName, String password){
        String sql = "insert into user value(null, ?, ?, ?)";
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();//盐随机数
        String encodedPasswod = new SimpleHash("md5",password,salt,2).toString();
        try(Connection conn = getConnect(); PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setString(1,userName);
            preparedStatement.setString(2,encodedPasswod);
            preparedStatement.setString(3,salt);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public User getUser(String userName){
        User user = null;
        String sql = "select * from user where name = ?";
        try(Connection conn = getConnect(); PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setString(1,userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setSalt(resultSet.getString("salt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;

    }

    public static void main(String[] args) {
        System.out.println(new DAO().listRole("zhang3"));
        System.out.println(new DAO().listRole("li4"));
        System.out.println(new DAO().listPermit("zhang3"));
        System.out.println(new DAO().listPermit("li4"));
//        new DAO().creatUser("zhao4", "abcd");
    }
}

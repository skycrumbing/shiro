package com.how2java;

import com.how2java.bean.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/17 0017.
 */
public class TestShiro {
    public static void main(String[] args){
//        User user1 = new User("zhang3","12345");
//        User user2 = new User("li4","abcde");
//        User user3 = new User("wang5","123456");
        User user4 = new User("zhao4","abcd");
        List<User> users = new ArrayList<User>();
//        users.add(user1);
//        users.add(user2);
//        users.add(user3);
        users.add(user4);

        String rolerAdmin = "admin";
        String roleProductManager = "productManager";
        List<String> roles = new ArrayList<>();
        roles.add(rolerAdmin);
        roles.add(roleProductManager);

        String permitAddProduct = "addProduct";
        String permitAddOrder = "addOrder";
        List<String> permits = new ArrayList<>();
        permits.add(permitAddOrder);
        permits.add(permitAddProduct);
        //模拟每个用户进行登陆操作
        for(User user:users){
            if(login(user)){
                System.out.printf("%s\t成功登陆，用的密码是 %s\t %n",user.getName(),user.getPassword());
            }else{
                System.out.printf("%s\t登陆失败，用的密码是 %s\t %n",user.getName(),user.getPassword());
            }
            //获取角色
            for(String role:roles){
                if(hasRole(role)) {
                    System.out.printf("%s\t拥有角色: %s\t%n", user.getName(), role);
                }
                    else{
                    System.out.printf("%s\t不拥有角色: %s\t%n",user.getName(),role);
                }
            }
            //获取权限
            for(String permit:permits){
                if(isPermit(permit)){
                    System.out.printf("%s\t拥有权限: %s\t%n",user.getName(),permit);
                }else{
                    System.out.printf("%s\t不拥有权限: %s\t%n",user.getName(),permit);
                }
            }
        }


    }

    private static Subject getSubject() {
        //加载配置文件，获得工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("E:\\code1\\shiro\\src\\main\\resource\\shiro.ini");
        //获取安全管理者实例
        SecurityManager securityManager = factory.getInstance();
        //将安全管理者放入全局对象
        SecurityUtils.setSecurityManager(securityManager);
        //全局对象通过安全管理者生成Subject对象
        Subject subject = SecurityUtils.getSubject();

        return subject;
    }

    private static boolean login(User user) {
        Subject subject = getSubject();
        //如果已经登陆，退出
        if(subject.isAuthenticated()){
            subject.logout();
        }
        //封装用户数据
        UsernamePasswordToken token = new UsernamePasswordToken(user.getName(),user.getPassword());
        try{
            //将用户的数据token 最终传递到Realm中进行对比
            subject.login(token);
        }catch (AuthenticationException e){
            //验证错误
            return false;
        }
        return subject.isAuthenticated();

    }

    private static boolean hasRole(String role){
        Subject subject = getSubject();
        return subject.hasRole(role);

    }

    private static boolean isPermit(String permit){
        Subject subject = getSubject();
        return subject.isPermitted(permit);

    }
}

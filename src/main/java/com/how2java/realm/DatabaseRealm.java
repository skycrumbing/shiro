package com.how2java.realm;

import com.how2java.bean.User;
import com.how2java.dao.DAO;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Set;
import java.util.SimpleTimeZone;

/**
 * Created by Administrator on 2018/9/17 0017.
 */
public class DatabaseRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //能进入到这里，表示账号已经通过验证了
        String usrName = (String) principalCollection.getPrimaryPrincipal();
        //通过DAO获取角色和权限
        Set<String> roles = new DAO().listRole(usrName);
        Set<String> permits = new DAO().listPermit(usrName);
        //授权对象
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        //把通过DAO获得的角色和权限放进去
        s.setStringPermissions(permits);
        s.setRoles(roles);
        return s;

    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取账号和密码
        UsernamePasswordToken t = (UsernamePasswordToken) authenticationToken;
        String userName = authenticationToken.getPrincipal().toString();
        String password =new String(t.getPassword());
        User user = new DAO().getUser(userName);
        //获取数据库中密码
        String passwordInDB = user.getPassword();
        //加密后再比较
        String salt = user.getSalt();
        String encodedPassword = new SimpleHash("md5", password, salt, 2).toString();
        //如果为空就是账号不存在，如果不相同就是密码错误，但是都抛出AuthenticationException，而不是抛出具体错误原因，免得给破解者提供帮助信息
        if(null==passwordInDB || !passwordInDB.equals(encodedPassword))
            throw new AuthenticationException();
        //认证信息里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名 :databaseRealm
        SimpleAuthenticationInfo a = new SimpleAuthenticationInfo(userName,password,getName());
        return a;
    }
}

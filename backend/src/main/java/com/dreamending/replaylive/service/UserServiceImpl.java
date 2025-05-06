package com.dreamending.replaylive.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dreamending.replaylive.entity.User;
import com.dreamending.replaylive.mapper.cl_usersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private cl_usersMapper usersMapper;
    //后台获取全部用户
    @Override
    public List<String> getAllUser() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "mail");

        List<User> users = usersMapper.selectList(queryWrapper);

        // 将User对象转换为字符串
        return users.stream()
                   .map(user -> String.format("ID: %s, 用户名: %s, 邮箱: %s",
                           user.getId(), user.getUsername(), user.getMail()))
                   .collect(Collectors.toList());
    }
    //登陆
    @Override
    public boolean validatePassword(String username, String password) {
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("username", "password") // 明确指定查询字段
                .eq("username", username)
                .eq("password", encryptedPassword);

        return usersMapper.selectOne(queryWrapper) != null;
    }
    @Override
    public boolean registerUser(String username, String password, String mail) {

        // 检查用户名是否已存在
        QueryWrapper<User> usernameQuery = new QueryWrapper<>();
        usernameQuery.eq("username", username);
        if (usersMapper.selectCount(usernameQuery) > 0) {
            return false;
        }

        // 检查邮箱是否已存在
        QueryWrapper<User> mailQuery = new QueryWrapper<>();
        mailQuery.eq("mail", mail);
        if (usersMapper.selectCount(mailQuery) > 0) {
            return false;
        }

        // 创建新用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setMail(mail);
        return usersMapper.insert(user) > 0;
    }
    @Override
    public boolean roleCheck(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("username", "role")
                .eq("username", username);  // 添加用户名查询条件

        User user = usersMapper.selectOne(queryWrapper);  // 改为查询单个用户

        // 添加空值判断并检查角色
        return user != null && "admin".equals(user.getRole());
    }

}

package com.fnbory.miaosha.dao;

import com.fnbory.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: fnbory
 * @Date: 2019/6/14 21:35
 */
@Mapper
public interface UserDao {
    @Select("select * from user where id = #{id}")
    public User getById(@Param("id")int id	);

    @Insert("insert into user(id, name)values(#{id}, #{name})")
    public int insert(User user);
}

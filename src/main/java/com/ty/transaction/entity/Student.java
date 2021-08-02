package com.ty.transaction.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Project: spring-transaction
 * @ClassName: Student
 * @Author: ty
 * @Description: 实体类
 * @Date: 2020/6/12 11:54
 * @Version: 1.0
 **/
@Data
@Accessors(chain = true)
@TableName("student")
public class Student {

    private String name;
    private Integer id;
    private String sex;
}

package com.ty.transaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ty.transaction.entity.Student;
import com.ty.transaction.exception.OtherException;

public interface StudentService extends IService<Student> {

    void modelOne(String newValue, String rollback) throws OtherException;

    void modelTwo(String newValue, String rollback) throws OtherException;

    void modelThree(String newValue, String rollback) throws OtherException;

    void isolationDefault(String newValue) throws OtherException;

    public void propagationRequiresNew(String newValue, String rollback);


    public void reset(String newValue, String rollback);
}

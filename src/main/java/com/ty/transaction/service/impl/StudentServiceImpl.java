package com.ty.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ty.transaction.entity.Student;
import com.ty.transaction.exception.OtherException;
import com.ty.transaction.mapper.StudentMapper;
import com.ty.transaction.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.concurrent.TimeUnit;

/**
 * @Project: spring-transaction
 * @ClassName: UserImpl
 * @Author: ty
 * @Description:
 * @Date: 2020/6/12
 * @Version: 1.0
 **/
@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {


    StudentServiceImpl studentService;
    @Autowired
    private ApplicationContext applicationContext;




    @Override
    /**
     * value: 设置事务管理器的bean的Name 例 value = "txManager"
     * transactionManager： 设置事务管理器的bean的Name 例 transactionManager = "txManager"
     * isolation： 设置事务的隔离级别 默认 Isolation.DEFAULT(使用后端数据库默认的隔离级别 如Mysql 默认REPEATABLE-READ)
     * propagation： 设置事务的传播行为 默认 Propagation.REQUIRED
     * rollbackFor： 设置允许回滚的非运行时异常,因为Spring只回滚运行时的异常不会回滚其他异常
     * noRollbackFor：设置允许提交的运行时异常
     * readOnly： 是否是只读事务 默认false
     * timeout： 事务的超时时间
     *
     */
    @Transactional(transactionManager = "txManager", isolation = Isolation.DEFAULT
            , propagation = Propagation.REQUIRED, rollbackFor = {OtherException.class}
            , noRollbackFor = {ArithmeticException.class})
    public void modelOne(String newValue, String rollback) throws OtherException {
        thisUpdate(newValue, 1);
        if ("+".equals(rollback)) {
            // 发生的ArithmeticException异常默认是回滚的
            System.out.println(1 / 0);
        } else if ("-".equals(rollback)) {
            // 发生的OtherException异常默认是提交的
            throw new OtherException("自定义异常,是否回滚");
        }
        thisUpdate(2, newValue + "edit");
    }

    @Override
    public void modelTwo(String newValue, String rollback) throws OtherException {
        thisUpdate(newValue, 1);
        if ("+".equals(rollback)) {
            // 发生的ArithmeticException异常默认是回滚的
            System.out.println(1 / 0);
        } else if ("-".equals(rollback)) {
            // 发生的OtherException异常默认是提交的
            throw new OtherException("自定义异常,是否回滚");
        }
        thisUpdate(2, newValue + "edit");
    }

    @Override
    public void modelThree(String newValue, String rollback) throws OtherException {
        thisUpdate(newValue, 1);
        if ("+".equals(rollback)) {
            // 发生的ArithmeticException异常默认是回滚的
            System.out.println(1 / 0);
        } else if ("-".equals(rollback)) {
            // 发生的OtherException异常默认是提交的
            throw new OtherException("自定义异常,是否回滚");
        }
        thisUpdate(2, newValue + "edit");
    }

    /**
     * isolation： 设置事务的隔离级别 默认 Isolation.DEFAULT(使用后端数据库默认的隔离级别 如Mysql 默认REPEATABLE-READ)
     * @param newValue
     * @throws OtherException
     */
    @Override
    @Transactional(isolation = Isolation.DEFAULT)
    public void isolationDefault(String newValue) throws OtherException {
        String threadName = Thread.currentThread().getName();
        if ("Thread1".equals(threadName)) {
            try {
                // 这里进行停止 主要是让线程二先把数据给修改了
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 查询ID为1的数据
        Student student = this.getById(1);
        if (student == null) {
            log.error( "未查到ID为1的数据,请添加ID为1的数据,方便展示效果");
            return;
        }
        log.info("{} : 修改前查询的数据: {}",Thread.currentThread().getName(), student.toString());
        // 修改
        boolean success = this.update(new UpdateWrapper<Student>()
                .lambda().set(Student::getName, newValue).eq(Student::getId, student.getId()));
        log.info( "{} : 修改后的数据 修改结果: {}",threadName,success);
        if (success) {
            student.setName(newValue);
        }
        // 回滚事务
        if ("Thread2".equals(threadName)) {
            try {
                // 这里进行停止 主要是不要让线程2执行结束,好让线程一来读线程二的数据
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if ("Thread2".equals(threadName)) {
            // 让线程二进行回滚,这样线程一读取了线程二修改的数据但是没有提交 造成脏数据
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info( "{} : 已经回滚",threadName);
        } else {
            // 这里回滚 是因为 不想改 数据库 方便多次测试 所以全部回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("{} : 修改后查询的数据: {}",Thread.currentThread().getName(), student.toString());
    }

    public void print(Student student) {
        log.info("{} : 数据: {}",Thread.currentThread().getName(), student.toString());
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void printData(String newValue) {

        System.err.println("修改的数据");

        this.list().forEach(System.err::println);

    }

    @Override
    @Transactional
    public void propagationRequiresNew(String newValue, String rollback) {
        boolean success = this.update(new UpdateWrapper<Student>()
                .lambda().set(Student::getName, newValue).eq(Student::getId, 1L));
        try {
            StudentService bean = applicationContext.getBean(StudentService.class);
            // 不能自动直接调用reset方法这样会事物失效,
            bean.reset(newValue, rollback);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void reset(String newValue, String rollback) {
        boolean success = this.update(new UpdateWrapper<Student>()
                .lambda().set(Student::getName, newValue + "最新值").eq(Student::getId, 1L));
        throw new RuntimeException("异常了...........");
    }

    private boolean thisUpdate(String newValue, Integer id) {
        boolean success = this.update(new UpdateWrapper<Student>()
                .lambda().set(Student::getName, newValue).eq(Student::getId, id));
        return success;
    }

    private boolean thisUpdate(Integer id, String newValue) {
        boolean success = this.update(new UpdateWrapper<Student>()
                .lambda().set(Student::getName, newValue).eq(Student::getId, id));
        return success;
    }


}

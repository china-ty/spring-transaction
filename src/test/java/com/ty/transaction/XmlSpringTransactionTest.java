package com.ty.transaction;

import com.ty.transaction.exception.OtherException;
import com.ty.transaction.service.StudentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Project: spring-transaction
 * @ClassName: XmlSpringTransactionTest
 * @Author: ty
 * @Description:
 * @Date: 2020/6/12
 * @Version: 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application.xml")
public class XmlSpringTransactionTest {

    @Autowired
    public StudentService studentService;


    /**
     * 使用方式一
     *
     * @throws OtherException
     */
    @Test
    public void model_one() throws OtherException {
        studentService.modelOne("李四", "-");
    }


    /**
     * 使用方式二
     *
     * @throws OtherException
     */
    @Test
    public void model_two() throws OtherException {
        studentService.modelTwo("张三", "+");
    }

    /**
     * 使用方式二
     *
     * @throws OtherException
     */
    @Test
    public void model_three() throws OtherException {
        studentService.modelThree("王麻子", "-");
    }

    //                         Isolation(隔离级别)
    @Test
    public void isolation_default() throws OtherException {
        Runnable run = () -> {
            try {
                String threadName = Thread.currentThread().getName();
                studentService.isolationDefault(threadName + ":更新值");
            } catch (OtherException e) {
                e.printStackTrace();
            }
        };
        // 修改了线程名 也要修改com.ty.transaction.service.StudentService.isolationDefault方法里面的
        new Thread(run, "Thread1").start();
        new Thread(run, "Thread2").start();
        try {
            // 等待上面线程执行完
            TimeUnit.SECONDS.sleep(12);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //                         Propagation(传播行为)
    @Test
    public void propagation_requires_new() throws OtherException {
        studentService.propagationRequiresNew("vvdzd", "-");
    }
}

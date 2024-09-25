package com.pxl.zkmall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {
    public static  ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start ........");
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//
//        }, executor);

//        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executor).whenComplete((res,exception) ->{
//            System.out.println("异步任务完成。。。结果是、："+res+";异常是："+exception);
//        }).exceptionally(throwable -> {
//            return 10;
//        });

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).handle((res,th)->{
            if ( res != null){
                return res*2;
            }
            if (th != null ){
                return 0;
            }
            return -1;
        });
        Integer integer = completableFuture.get();
        System.out.println("main end ........" + integer );
    }


    public static void thread(String[] args) {
        System.out.println("main start ........");
        /**
         * 1）、继承 Thread
         *    new Thread01().start();
         * 2）、实现 Runnable 接口
         * Runable01 runable01 = new Runable01();
         *         new Thread(runable01).start();
         * 3）、实现 Callable 接口 + FutureTask （可以拿到返回结果，可以处理异常）
         * 4）、线程池
         *
         * 七大参数
         * int corePoolSize,核心线程数；线程池创建好以后就准备就绪的线程数量，就等待来接受异步任务取执行
         * int maximumPoolSize,最大线程数量；控制资源并发
         * long keepAliveTime,存活时间；空闲线程的存活时间，当线程池中线程数量超过core，空闲时间达到keepAliveTime，
         * 多余的空闲线程会被销毁直到只剩下core个线程为止
         *TimeUnit unit, 时间单位
         *BlockingQueue<Runnable> workQueue, 阻塞队列 如果任务有很多，就会将目前多的任务放在队列里，只要有空闲线程，就会去队列里面取任务
         *ThreadFactory threadFactory,线程的创建工厂
         *RejectedExecutionHandler handler 如果队列满了，并且线程达到最大线程数量，就会采取任务拒绝策略
         *
         * 工作顺序
         * 、线程池创建，准备好 core 数量的核心线程，准备接受任务
         * 2、新的任务进来，用 core 准备好的空闲线程执行。
         * (1) 、core 满了，就将再进来的任务放入阻塞队列中。空闲的 core 就会自己去阻塞队
         * 列获取任务执行
         * (2) 、阻塞队列满了，就直接开新线程执行，最大只能开到 max 指定的数量
         * (3) 、max 都执行好了。Max-core 数量空闲的线程会在 keepAliveTime 指定的时间后自动销毁。最终保持到 core 大小
         * (4) 、如果线程数开到了 max 的数量，还有新任务进来，就会使用 reject 指定的拒绝策略进行处理
         * 3、所有的线程创建都是由指定的 factory 创建的
         */

//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
        System.out.println("main end ........");

    }
    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i = 10 /2;
            System.out.println("运行结果:"+i);
        }
    }
    public static class Runable01 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i = 10 /2;
            System.out.println("运行结果:"+i);
        }
    }
}

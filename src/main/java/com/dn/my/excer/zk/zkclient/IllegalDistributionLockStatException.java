package com.dn.my.excer.zk.zkclient;

/**
 * @ClassName : IllegalDistributionLockStatException
 * @Description :分布式队列任务异常时
 * @Author :hjh
 * @Date:2019/10/6 22:31
 * @Version 1.0
 **/
public class IllegalDistributionLockStatException extends RuntimeException {
    private static final long serialVersionUID=1L;
    private State state=State.OTHER;
    public IllegalDistributionLockStatException(){super();}
    public IllegalDistributionLockStatException(String msg){super(msg);}
    public IllegalDistributionLockStatException(State illegalState){
        super();
        this.state=illegalState;
    }
    public IllegalDistributionLockStatException(String msg,Throwable cause){
        super(msg,cause);
    }
    public State getState(){
        return state;
    }
    public enum State{
        OTHER,
        EMPTY,
        FULL;
    }
}

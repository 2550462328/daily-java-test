package cn.zhanghui.demo.daily.base.thread.designmodel.futuremodel;

public class FutureData implements Data {
    private RealData realData;
    private boolean isReady = false;

    public synchronized void setRealData(RealData realData) {
        //如果realData装载完毕，直接返回
        if (isReady) {
            return;
        }
        //如果没装载，装载真实对象
        this.realData = realData;
        isReady = true;
        notify();
    }

    @Override
    public synchronized String getRequest() {
        //如果realData没装载好，程序一直处于堵塞状态
        while (!isReady) {
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //装载好了就直接返回数据
        return this.realData.getRequest();
    }
}

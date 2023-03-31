package cn.zhanghui.demo.daily.base.thread.designmodel.futuremodel;

public class FutureClient {

    final FutureData futureData = new FutureData();

    public Data request(final String queryStr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RealData realData = new RealData(queryStr);
                futureData.setRealData(realData);
            }
        }).start();
        return futureData;
    }

}

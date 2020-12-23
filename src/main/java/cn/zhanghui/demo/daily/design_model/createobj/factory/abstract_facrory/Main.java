package cn.zhanghui.demo.daily.design_model.createobj.factory.abstract_facrory;

public class Main {
	public static void main(String[] args) {
		AbstractFactory abstractFactory = new AbstractFactory();
		MiniCar miniCar = (MiniCar) abstractFactory.createCar();
		Banala banala = (Banala) abstractFactory.createFood();
		PlayPlane playPlane = (PlayPlane) abstractFactory.createPlay();
		miniCar.run();
		banala.eating();
		playPlane.play();
	}
}

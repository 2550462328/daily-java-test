package cn.zhanghui.demo.daily.netty.heartbeat;

import java.util.HashMap;

public class RequestInfo {
    private String ip;
    private HashMap<String, Object> cpuPercMap;
    private HashMap<String, Object> memoryMap;
    
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public HashMap<String, Object> getCpuPercMap() {
		return cpuPercMap;
	}
	public void setCpuPercMap(HashMap<String, Object> cpuPercMap) {
		this.cpuPercMap = cpuPercMap;
	}
	public HashMap<String, Object> getMemoryMap() {
		return memoryMap;
	}
	public void setMemoryMap(HashMap<String, Object> memoryMap) {
		this.memoryMap = memoryMap;
	}
    
    
}

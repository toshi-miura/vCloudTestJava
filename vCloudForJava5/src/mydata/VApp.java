package mydata;

import java.util.List;
import java.util.Map;

public class VApp {

	private String name;
	private String extProjCode;
	private Map<String, VM> vmMap;

	private User owner;
	private List<User> users;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExtProjCode() {
		return extProjCode;
	}
	public void setExtProjCode(String extProjCode) {
		this.extProjCode = extProjCode;
	}
	public Map<String, VM> getVmMap() {
		return vmMap;
	}
	public void setVmMap(Map<String, VM> vmMap) {
		this.vmMap = vmMap;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}


}

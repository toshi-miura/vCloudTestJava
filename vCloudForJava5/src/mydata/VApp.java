package mydata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.admin.User;

/**
 * 目的： 必要なパラメータに関しアクセスを簡易化する。 通信が絡むと思われるところをキャッシュする。 必要な拡張フィールドを定義する
 *
 * @author user
 *
 */
public class VApp {

	private Vapp vapp;

	private String extProjCode;
	private Map<String, mydata.VM> vmMap = new HashMap<String, mydata.VM>();

	private mydata.User owner;
	private List<mydata.User> users;

	private VcloudClient vcloudClient;

	public VApp(Vapp vapp, VcloudClient vcloudClient) throws VCloudException {
		super();
		this.vapp = vapp;

		this.vcloudClient = vcloudClient;
		init();
	}

	public String getName() throws VCloudException {
		return vapp.getReference().getName();
	}

	public String getExtProjCode() {
		return extProjCode;
	}

	public void setExtProjCode(String extProjCode) {
		this.extProjCode = extProjCode;
	}

	public Map<String, mydata.VM> getVmMap() {
		return vmMap;
	}

	public mydata.User getOwner() {
		return owner;
	}

	public List<mydata.User> getUsers() {
		return users;
	}

	public int getCpu() throws VCloudException {

		int sum = 0;
		for (mydata.VM vm : vmMap.values()) {
			sum = +vm.getCpu();
		}
		return sum;
	}

	public int getMemorySizeMB() throws VCloudException {
		int sum = 0;
		for (mydata.VM vm : vmMap.values()) {
			sum = +vm.getMemorySizeMB();
		}
		return sum;

	}

	public int getTotalHDDGB() throws VCloudException {
		int sum = 0;
		for (mydata.VM vm : vmMap.values()) {
			sum = +vm.getTotalHDDGB();
		}
		return sum;

	}

	@Override
	public String toString() {

		try {
			String r =
					"APPNAME:	" + getName() + "\n" +
					"owner:	" + owner	+ "\n" +
					"VMNo:	" + vmMap.size() + "\n"+
					"CPUNum:	" + getCpu() + "\n"+
					"MemNum:	" + getMemorySizeMB() + "\n"+
					"HDDNum:	" + getTotalHDDGB() + "\n"
					;

			return r;
		} catch (VCloudException e) {

			return e.getMessage();
		}
	}

	public void setVapp(Vapp vapp) throws VCloudException {
		this.vapp = vapp;
		init();
	}

	private void init() throws VCloudException {
		// VMに関するINIT
		List<VM> vms = vapp.getChildrenVms();
		for (VM vm : vms) {
			mapVM(vm);

		}

		// OWNER関連
		mydata.User owner = getVAppOwner();


		this.owner = owner;

		// TODO 権限関連

	}

	private void mapVM(VM vmwareVm) throws VCloudException {

		mydata.VM vm = new mydata.VM(vmwareVm);

		vmMap.put(vm.getName(), vm);

	}

	private mydata.User getVAppOwner() {

		ReferenceType vAppRef=vapp.getReference();
		mydata.User r;

		try {
			ReferenceType owner = Vapp.getOwner(vcloudClient, vAppRef);
			User user = User.getUserByReference(vcloudClient, owner);

			UserType resource = user.getResource();

			r = new mydata.User(resource);
		} catch (VCloudException e) {
			// エラーの原因
			// 現状見えているのは権限不足
			// マスターユーザーのものは見えないようだ。

			r = mydata.User.VCD_MASTER;
		}

		return r;
	}

}

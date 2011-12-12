package mydata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vmware.vcloud.api.rest.schema.AccessSettingType;
import com.vmware.vcloud.api.rest.schema.AccessSettingsType;
import com.vmware.vcloud.api.rest.schema.ControlAccessParamsType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.sdk.Metadata;
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
	private List<mydata.User> users =new ArrayList<mydata.User>();

	private VcloudClient vcloudClient;

	private Metadata metadata;

	/**
	 * equals実装用のテンポラリフィールド
	 */
	private String _name;



	public VApp(Vapp vapp, VcloudClient vcloudClient) throws VCloudException {
		super();
		this.vapp = vapp;
		metadata = this.vapp.getMetadata();

		this.vcloudClient = vcloudClient;
		init();
	}


	protected void setMetadata(String k,String v) throws VCloudException{

		metadata.updateMetadataEntry(k, v);
	}

	protected HashMap<String,String> getMetadata() throws VCloudException{

		return metadata.getMetadataEntries();
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
			sum += vm.getCpu();
		}
		return sum;
	}

	public int getMemorySizeMB() throws VCloudException {
		int sum = 0;
		for (mydata.VM vm : vmMap.values()) {
			sum += vm.getMemorySizeMB();
		}
		return sum;

	}

	public int getTotalHDDGB() throws VCloudException {
		int sum = 0;
		for (mydata.VM vm : vmMap.values()) {
			sum += vm.getTotalHDDGB();
		}
		return sum;

	}

	@Override
	public String toString() {

		try {
			String r = "APPNAME:	" + getName() + "\n" + "owner:	" + owner
					+ "\n" + "VMNo:	" + vmMap.size() + "\n" + "CPUNum:	"
					+ getCpu() + "\n" + "MemNum:	" + getMemorySizeMB() + "\n"
					+ "HDDNum:	" + getTotalHDDGB() + "\n";
			StringBuilder sbBuilder = new StringBuilder();

			int i = 0;
			for (mydata.VM vm : vmMap.values()) {
				sbBuilder.append("no" + i++ + "\t").append(vm).append("\n");
			}

			int j=0;
			for (mydata.User user : users) {
				sbBuilder.append("user" + j++ + "\t").append(user).append("\n");
			}


			return r + sbBuilder.toString();
		} catch (VCloudException e) {

			return e.getMessage();
		}
	}

	public void setVapp(Vapp vapp) throws VCloudException {
		this.vapp = vapp;
		init();
	}

	private void init() throws VCloudException {
		_name=getName();

		// VMに関するINIT
		List<VM> vms = vapp.getChildrenVms();
		for (VM vm : vms) {
			mapVM(vm);

		}

		// OWNER関連
		mydata.User owner = getVAppOwner();

		this.owner = owner;


		mapUser();



	}

	private void mapUser() throws VCloudException {

		ControlAccessParamsType controlAccess = vapp.getControlAccess();
		AccessSettingsType accessSettings = controlAccess.getAccessSettings();
		List<AccessSettingType> accessSetting = accessSettings
				.getAccessSetting();

		for (AccessSettingType accessSettingType : accessSetting) {

			ReferenceType subject = accessSettingType.getSubject();


			User user = User.getUserByReference(vcloudClient, subject);
			UserType resource = user.getResource();
			mydata.User r = new mydata.User(resource);

			users.add(r);

		}

	}

	private void mapVM(VM vmwareVm) throws VCloudException {

		mydata.VM vm = new mydata.VM(vmwareVm);

		vmMap.put(vm.getName(), vm);

	}

	private mydata.User getVAppOwner() {

		ReferenceType vAppRef = vapp.getReference();
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


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VApp other = (VApp) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		return true;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		return result;
	}



}

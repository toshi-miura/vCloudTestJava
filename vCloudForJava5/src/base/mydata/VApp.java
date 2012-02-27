package base.mydata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.vmware.vcloud.api.rest.schema.AccessSettingType;
import com.vmware.vcloud.api.rest.schema.AccessSettingsType;
import com.vmware.vcloud.api.rest.schema.ControlAccessParamsType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.User;

/**
 * 目的： 必要なパラメータに関しアクセスを簡易化する。 通信が絡むと思われるところをキャッシュする。 必要な拡張フィールドを定義する
 *
 * @author user
 *
 */
public class VApp extends VObj {

	protected Vapp vapp;
	protected String vcdName;

	protected Map<String, base.mydata.VM> vmMap = new HashMap<String, base.mydata.VM>();

	protected base.mydata.User owner;
	protected List<base.mydata.User> users = new ArrayList<base.mydata.User>();

	protected VcloudClient vcloudClient;

	/**
	 * equals実装用のテンポラリフィールド
	 */
	private String _name;

	protected VApp() {
	}

	public VApp(String vcdName, Vapp vapp, VcloudClient vcloudClient)
			throws VCloudException {
		super(vapp);
		this.vapp = vapp;
		this.vcdName = vcdName;

		this.vcloudClient = vcloudClient;
		init();
	}

	public VApp(VApp app) throws VCloudException {

		this(app.vcdName, app.vapp, app.vcloudClient);
	}

	public String getID() throws VCloudException {
		return vapp.getReference().getId();
	}

	public String getName() throws VCloudException {

		return vapp.getReference().getName();
	}

	public Map<String, base.mydata.VM> getVmMap() {
		return vmMap;
	}

	public base.mydata.User getOwner() {
		return owner;
	}

	public List<base.mydata.User> getUsers() {
		return users;
	}

	/**
	 * オーナーと使用権限を持っている人すべて。
	 * @return
	 */
	public List<base.mydata.User> getAllUsers() {
		ArrayList<base.mydata.User> list = new ArrayList<base.mydata.User>();
		list.addAll(getUsers());
		list.add(getOwner());
		return list;
	}

	/**
	 * すべての人間のメールアドレス。
	 * @return
	 */
	public List<String> getAllMailAddress() {

		List<base.mydata.User> allUsers = getAllUsers();
		List<String> transform = Lists.transform(allUsers,
				new Function<base.mydata.User, String>() {
					@Override
					public String apply(base.mydata.User arg0) {

						return arg0.getEmailAddress();
					}
				});

		return transform;

	}

	public int getCpu() throws VCloudException {

		int sum = 0;
		for (base.mydata.VM vm : vmMap.values()) {
			sum += vm.getCpu();
		}
		return sum;
	}

	public int getMemorySizeMB() throws VCloudException {
		int sum = 0;
		for (base.mydata.VM vm : vmMap.values()) {
			sum += vm.getMemorySizeMB();
		}
		return sum;

	}

	public int getTotalHDDGB() throws VCloudException {
		int sum = 0;
		for (base.mydata.VM vm : vmMap.values()) {
			sum += vm.getTotalHDDGB();
		}
		return sum;

	}

	/**
	 * 人間判読用。
	 * このVAPPの情報のみ。
	 * toStringはこの情報も出す
	 * @return
	 */
	public String toBaseString() {
		try {
			return "APPNAME:	" + getName() + "\t" + "owner:	" + owner + "\t"
					+ "VMNo:	" + vmMap.size() + "\t" + "CPUNum:	" + getCpu()
					+ "\t" + "MemNum:	" + getMemorySizeMB() + "\t" + "HDDNum:	"
					+ getTotalHDDGB();
		} catch (VCloudException e) {

			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * 文字列での状態比較用
	 * @return
	 */
	public String toBaseSimpleInfo() {
		try {
			return getName() + "\t" + owner + "\t" + vmMap.size() + "\t"
					+ getCpu() + "\t" + getMemorySizeMB() + "\t"
					+ getTotalHDDGB();
		} catch (VCloudException e) {

			e.printStackTrace();
			return e.getMessage();
		}
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
			for (base.mydata.VM vm : vmMap.values()) {
				sbBuilder.append("no" + i++ + "\t").append(vm).append("\n");
			}

			int j = 0;
			for (base.mydata.User user : users) {
				sbBuilder.append("user" + j++ + "\t").append(user).append("\n");
			}

			return r + sbBuilder.toString();
		} catch (VCloudException e) {

			return e.getMessage();
		}
	}

	public Vapp getVcdVapp() {
		return vapp;
	}

	private void init() throws VCloudException {
		_name = getName();

		// VMに関するINIT
		List<VM> vms = vapp.getChildrenVms();
		for (VM vm : vms) {
			mapVM(vm);

		}

		// OWNER関連
		base.mydata.User owner = getVAppOwner();

		this.owner = owner;

		mapUser();

	}

	private void mapUser() throws VCloudException {

		ControlAccessParamsType controlAccess = vapp.getControlAccess();
		AccessSettingsType accessSettings = controlAccess.getAccessSettings();

		if (accessSettings != null) {
			List<AccessSettingType> accessSetting = accessSettings
					.getAccessSetting();

			for (AccessSettingType accessSettingType : accessSetting) {

				ReferenceType subject = accessSettingType.getSubject();

				User user = User.getUserByReference(vcloudClient, subject);
				UserType resource = user.getResource();
				System.out.println(resource.getName());
				base.mydata.User r = new base.mydata.User(resource);

				users.add(r);

			}
		}

	}

	private void mapVM(VM vmwareVm) throws VCloudException {

		base.mydata.VM vm = new base.mydata.VM(vmwareVm);

		vmMap.put(vm.getName(), vm);

	}

	private base.mydata.User getVAppOwner() {

		ReferenceType vAppRef = vapp.getReference();
		base.mydata.User r;

		try {
			ReferenceType owner = Vapp.getOwner(vcloudClient, vAppRef);
			User user = User.getUserByReference(vcloudClient, owner);

			UserType resource = user.getResource();

			r = new base.mydata.User(resource);
		} catch (VCloudException e) {
			// エラーの原因
			// 現状見えているのは権限不足
			// マスターユーザーのものは見えないようだ。

			r = base.mydata.User.VCD_MASTER;
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

	public String getVcdName() {
		return vcdName;
	}

}

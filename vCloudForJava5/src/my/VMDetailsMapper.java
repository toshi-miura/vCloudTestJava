package my;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mydata.VApp;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;

/**
 * TODO
 * <li>シングルトン的にする。</li>
 * <li>キャッシュのマネージャ的なものにする</li>
 * <li>耐久試験（接続維持時間）を調べる</li>
 * <li>clientのリロード機能を入れる</li>
 *
 *
 */
public class VMDetailsMapper {

	private final VcloudClient vcloudClient;

	public VMDetailsMapper(VcloudClient vcloudClient) {
		this.vcloudClient = vcloudClient;
	}

	public VMDetailsMapper(String url, String user, String pass)
			throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, VCloudException {
		this.vcloudClient = Util.login(url, user, pass);
	}

	private final HashMap<String, Set<VApp>> vappMap = new HashMap<String, Set<VApp>>();

	public Set<String> getVCDNameSet() {
		return vappMap.keySet();

	}

	public synchronized void run() throws VCloudException {

		vappMap.clear();
		HashMap<String, ReferenceType> orgsList = vcloudClient
				.getOrgRefsByName();
		for (ReferenceType orgRef : orgsList.values()) {
			for (ReferenceType vdcRef : Organization
					.getOrganizationByReference(vcloudClient, orgRef)
					.getVdcRefs()) {

				Vdc vdc = Vdc.getVdcByReference(vcloudClient, vdcRef);

				for (ReferenceType vAppRef : Vdc.getVdcByReference(
						vcloudClient, vdcRef).getVappRefs()) {

					VApp vApp = mapVApp(vdcRef.getName(), vAppRef);

					put(vdcRef.getName(), vApp);

				}

			}
		}

	}

	public VApp refresh(VApp vapp) throws VCloudException {

		Vapp vcdVapp = vapp.getVcdVapp();

		ReferenceType reference = vcdVapp.getReference();
		VApp newApp = mapVApp(vapp.getVcdName(), reference);
		synchronized (this) {

			vappMap.get(vapp.getVcdName()).remove(vapp);
			vappMap.get(newApp.getVcdName()).add(newApp);

		}
		return newApp;
	}

	public Set<VApp> refresh(Set<? extends VApp> vappSet)
			throws VCloudException {

		Set<VApp> result = new HashSet<VApp>();
		for (VApp vApp2 : vappSet) {
			result.add(refresh(vApp2));
		}
		return result;

	}

	private void put(String vcdName, VApp app) {
		Set<VApp> set = vappMap.get(vcdName);
		if (set == null) {
			set = new HashSet<VApp>();
			vappMap.put(vcdName, set);

		}
		set.add(app);

	}

	private VApp mapVApp(String vcdName, ReferenceType vAppRef)
			throws VCloudException {

		/*
		 * System.out.println("	Vapp_OtherAttributes : " +
		 * vAppRef.getOtherAttributes().size());
		 * System.out.println("	Vapp_VCloudExtension : " +
		 * vAppRef.getVCloudExtension().size());
		 */

		// vcloudClient.getVcloudAdmin()ext.
		// vcloudClient.getVcloudAdminExtension().

		Vapp vapp = Vapp.getVappByReference(vcloudClient, vAppRef);

		VApp app = new VApp(vcdName, vapp, vcloudClient);

		return app;

	}

	public synchronized HashMap<String, Set<VApp>> getVappMap() {
		return vappMap;
	}

	public synchronized Set<VApp> getVappSet(String vcdNamd) {
		return vappMap.get(vcdNamd);
	}

	/**
	 * 当面は、１データセンターしか想定しないので必要ない
	 *
	 * @param userid
	 * @return
	 */
	public synchronized HashMap<String, Set<VApp>> getVappSetByUser(
			String userid) {

		throw new IllegalStateException("未実装。呼ぶな");

	}

	/**
	 *
	 */
	public synchronized Set<VApp> getVappSetByUser(String vcdNamd, String userid) {
		Set<VApp> vappSet = getVappSet(vcdNamd);
		Set<VApp> resultSet = new HashSet<VApp>();

		resultSet.addAll(filterOnwer(vappSet, userid));
		resultSet.addAll(filterUser(vappSet, userid));

		return resultSet;

	}

	public synchronized Set<VApp> getVappSetByName(String vcdNamd,
			String userid, String vAppName) throws VCloudException {
		Set<VApp> vappSet = getVappSet(vcdNamd);
		Set<VApp> tempSet = new HashSet<VApp>();

		tempSet.addAll(filterOnwer(vappSet, userid));
		tempSet.addAll(filterUser(vappSet, userid));

		return filterVappName(vappSet, vAppName);

	}

	/**
	 * 指定されたユーザがオーナのvAPPを返す。
	 * @param vappSet
	 * @param userid
	 * @return
	 */
	private Set<VApp> filterOnwer(Set<VApp> vappSet, String userid) {
		Set<VApp> resultSet = new HashSet<VApp>();
		for (VApp vApp : vappSet) {
			if (userid.equals(vApp.getOwner().getNameInSource())) {
				resultSet.add(vApp);
			}
		}
		return resultSet;

	}

	/**
	 * 指定されたユーザがオーナのvAPPを返す。
	 * @param vappSet
	 * @param userid
	 * @return
	 * @throws VCloudException
	 */
	private Set<VApp> filterVappName(Set<VApp> vappSet, String vappName)
			throws VCloudException {
		Set<VApp> resultSet = new HashSet<VApp>();
		for (VApp vApp : vappSet) {
			if (vApp.getName().startsWith(vappName)) {
				resultSet.add(vApp);
			}
		}
		return resultSet;

	}

	/**
	 * 指定されたユーザがアクセス可能なvAPPを返す。
	 * @param vappSet
	 * @param userid
	 * @return
	 */
	private Set<VApp> filterUser(Set<VApp> vappSet, String userid) {
		Set<VApp> resultSet = new HashSet<VApp>();
		for (VApp vApp : vappSet) {
			if (userid.equals(vApp.getOwner().getNameInSource())) {
				resultSet.add(vApp);
			}
		}
		return resultSet;
	}

}

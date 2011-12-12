package my;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mydata.VApp;

import org.apache.http.HttpException;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.api.rest.schema.VCloudExtensionType;
import com.vmware.vcloud.sdk.Metadata;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.admin.User;

/**
 * TODO シングルトン的にする。 キャッシュのマネージャ的なものにする 耐久試験（接続維持時間）を調べる clientのリロード機能を入れる
 *
 *
 */
public class VMDetailsMapper {

	private VcloudClient vcloudClient;

	public VMDetailsMapper(VcloudClient vcloudClient) {
		this.vcloudClient = vcloudClient;
	}

	public VMDetailsMapper(String url, String user, String pass)
			throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, VCloudException {
		this.vcloudClient = Util.login(url, user, pass);
	}

	private HashMap<String, Set<VApp>> vappMap = new HashMap<String, Set<VApp>>();

	public void run() throws HttpException, VCloudException, IOException,
			KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, KeyStoreException {

		HashMap<String, ReferenceType> orgsList = vcloudClient
				.getOrgRefsByName();
		for (ReferenceType orgRef : orgsList.values()) {
			for (ReferenceType vdcRef : Organization
					.getOrganizationByReference(vcloudClient, orgRef)
					.getVdcRefs()) {

				Vdc vdc = Vdc.getVdcByReference(vcloudClient, vdcRef);

				for (ReferenceType vAppRef : Vdc.getVdcByReference(
						vcloudClient, vdcRef).getVappRefs()) {

					VApp vApp = mapVApp(vAppRef);

					put(vdcRef.getName(), vApp);

				}

			}
		}

	}

	private void put(String vcdName, VApp app) {
		Set<VApp> set = vappMap.get(vcdName);
		if (set == null) {
			set = new HashSet<VApp>();
			vappMap.put(vcdName, set);
			System.out.println("PUT NEW VCD:" + vcdName);
		}
		set.add(app);

	}

	private VApp mapVApp(ReferenceType vAppRef) throws VCloudException {

		/*
		 * System.out.println("	Vapp_OtherAttributes : " +
		 * vAppRef.getOtherAttributes().size());
		 * System.out.println("	Vapp_VCloudExtension : " +
		 * vAppRef.getVCloudExtension().size());
		 */

		// vcloudClient.getVcloudAdmin()ext.
		// vcloudClient.getVcloudAdminExtension().

		Vapp vapp = Vapp.getVappByReference(vcloudClient, vAppRef);

		VApp app = new VApp(vapp, vcloudClient);

		return app;

	}

	public HashMap<String, Set<VApp>> getVappMap() {
		return vappMap;
	}

	public Set<VApp> getVappSet(String vcdNamd) {
		return vappMap.get(vcdNamd);
	}

	/**
	 * 当面は、１データセンターしか想定しないので必要ない
	 *
	 * @param userid
	 * @return
	 */
	public HashMap<String, Set<VApp>> getVappSetByUser(String userid) {

		throw new IllegalStateException("未実装。呼ぶな");

	}

	/**
	 *
	 */
	public Set<VApp> getVappSetByUser(String vcdNamd, String userid) {
		Set<VApp> vappSet = getVappSet(vcdNamd);
		Set<VApp> resultSet = new HashSet<VApp>();

		resultSet.addAll(filterOnwer(vappSet, userid));
		resultSet.addAll(filterUser(vappSet, userid));

		return resultSet;

	}

	private static Set<VApp> filterOnwer(Set<VApp> vappSet, String userid) {
		Set<VApp> resultSet = new HashSet<VApp>();
		for (VApp vApp : vappSet) {
			if (userid.equals(vApp.getOwner().getNameInSource())) {
				resultSet.add(vApp);
			}
		}
		return resultSet;

	}

	private static Set<VApp> filterUser(Set<VApp> vappSet, String userid) {
		Set<VApp> resultSet = new HashSet<VApp>();
		for (VApp vApp : vappSet) {
			if (userid.equals(vApp.getOwner().getNameInSource())) {
				resultSet.add(vApp);
			}
		}
		return resultSet;
	}

}

package my;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.List;

import mydata.VApp;

import org.apache.http.HttpException;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.admin.User;

/**
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



	public void run() throws HttpException, VCloudException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, KeyStoreException {



		HashMap<String, ReferenceType> orgsList = vcloudClient
				.getOrgRefsByName();
		for (ReferenceType orgRef : orgsList.values()) {
			for (ReferenceType vdcRef : Organization
					.getOrganizationByReference(vcloudClient, orgRef)
					.getVdcRefs()) {

				Vdc vdc = Vdc.getVdcByReference(vcloudClient, vdcRef);
				System.out.println("Vdc : " + vdcRef.getName() + " : "
						+ vdc.getResource().getAllocationModel());

				for (ReferenceType vAppRef : Vdc.getVdcByReference(
						vcloudClient, vdcRef).getVappRefs()) {

					Vapp vApp = mapVApp( vAppRef);

				}

			}
		}

	}

	private Vapp mapVApp(  ReferenceType vAppRef)
			throws VCloudException {

		VApp app = new VApp();

		app.setName(vAppRef.getName());

		/*
		 * 使えるなら、この辺のアトリビュートを管理用に使いたい
		 *
		 * System.out.println("	Vapp_OtherAttributes : " +
		 * vAppRef.getOtherAttributes().size());
		 * System.out.println("	Vapp_VCloudExtension : " +
		 * vAppRef.getVCloudExtension().size());
		 */

		mydata.User owner = vAppOwner( vAppRef);


		/**
		 * AccessSettingsType settings = controlAccess .getAccessSettings();
		 *
		 * if (settings != null) {
		 *
		 * for (AccessSettingType set : settings .getAccessSetting()) {
		 * AccessLevelType accessLevel = set.getAccessLevel();
		 *
		 *
		 * System.out.println("	Vapp_AccessLevel : " + accessLevel.name() + "\t"
		 * + set.getSubject().getHref()); User user =
		 * User.getUserByReference(vcloudClient, set.getSubject());
		 * System.out.println("	Vapp_UserInfoMail : " +
		 * user.getResource().getEmailAddress());
		 * System.out.println("	Vapp_UserInfo : " +
		 * user.getResource().getName()); } }else{
		 * System.out.println("	Vapp_ACC_INFO_NULL : "); }
		 */

		Vapp vapp = Vapp.getVappByReference(vcloudClient, vAppRef);
		showVMInfo(vapp);

		return vapp;

	}

	private void showVMInfo(Vapp vapp) throws VCloudException {
		List<VM> vms = vapp.getChildrenVms();
		for (VM vm : vms) {
			showVMInfo(vm);

		}
	}

	private void showVMInfo(VM vm) throws VCloudException {
		System.out.println("		Vm : " + vm.getResource().getName());
		System.out.println("			Status : " + vm.getVMStatus());
		System.out.println("			CPU : " + vm.getCpu().getNoOfCpus());
		System.out.println("			Memory : " + vm.getMemory().getMemorySize()
				+ " Mb");
		for (VirtualDisk disk : vm.getDisks()) {
			if (disk.isHardDisk())
				System.out.println("			HardDisk : " + disk.getHardDiskSize()
						+ " Mb");
		}
	}

	private mydata.User vAppOwner(
			ReferenceType vAppRef) {

		mydata.User r;

		try {
			ReferenceType owner = Vapp.getOwner(vcloudClient, vAppRef);
			System.out.println(owner);
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

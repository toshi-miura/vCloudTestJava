package my;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
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

					VApp vApp = mapVApp( vAppRef);
					System.out.println(vApp);

				}

			}
		}

	}

	public VApp mapVApp(  ReferenceType vAppRef)
			throws VCloudException {


		  System.out.println("	Vapp_OtherAttributes : " +
		  vAppRef.getOtherAttributes().size());
		  System.out.println("	Vapp_VCloudExtension : " +
		  vAppRef.getVCloudExtension().size());



		  // vcloudClient.getVcloudAdmin()ext.
		  // vcloudClient.getVcloudAdminExtension().



		//TODO
		//説明のフィールドを利用する手もあり




		Vapp vapp = Vapp.getVappByReference(vcloudClient, vAppRef);

		Metadata metadata = vapp.getMetadata();

		System.out.println("★"+ metadata.getMetadataEntries().size());
		
		Set<Entry<String,String>> entrySet = metadata.getMetadataEntries().entrySet();
		for (Entry<String,String> e : entrySet) {
			System.out.println(e.getKey()+"    "+e.getValue());

		}


		metadata.updateMetadataEntry("登録済み", "YES");
		metadata.updateMetadataEntry("プロジェクトは？", "Cです。");
		metadata.updateMetadataEntry("課金額は？", "XXXXです");
		metadata.updateMetadataEntry("ほげほげ", "ほげほげ");


		VApp app = new VApp(vapp,vcloudClient);




		return app;

	}




}

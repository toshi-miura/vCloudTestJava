package base.mydata;

import java.util.Collection;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utconf.Conf;
import base.my.Util;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Catalog;
import com.vmware.vcloud.sdk.Metadata;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.ProviderVdc;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;

public class VdcTest {

	private static VcloudClient vcloudClient = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		vcloudClient = Util.login(Conf.HOST, Conf.USER, Conf.PASS);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws VCloudException {
		HashMap<String, ReferenceType> orgsList = vcloudClient
				.getOrgRefsByName();
		for (ReferenceType orgRef : orgsList.values()) {

			try {
				Organization organizationByReference = Organization
						.getOrganizationByReference(vcloudClient, orgRef);

				System.out.println(orgRef.getName());
				Metadata metadata = organizationByReference.getMetadata();
				metadata.updateMetadataEntry("aaa", "aaa");

			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			try {
				System.out
						.println("providerVdcRefs-----------------------------");

				VcloudAdmin vcloudAdmin = vcloudClient.getVcloudAdmin();
				Collection<ReferenceType> providerVdcRefs = vcloudAdmin
						.getProviderVdcRefs();

				System.out.println(providerVdcRefs.size());

				for (ReferenceType referenceType : providerVdcRefs) {

					System.out.println(referenceType.getName());

					ProviderVdc vdcByReference = ProviderVdc
							.getProviderVdcByReference(vcloudClient,
									referenceType);
					Metadata metadata = vdcByReference.getMetadata();
					metadata.updateMetadataEntry("aaa", "aaa");

					System.out.println(metadata.getMetadataEntry("aaa"));

				}

			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			try {
				System.out
						.println("catalogByReference-----------------------------");
				Organization organizationByReference = Organization
						.getOrganizationByReference(vcloudClient, orgRef);
				Collection<ReferenceType> catalogRefs = organizationByReference
						.getCatalogRefs();

				for (ReferenceType referenceType : catalogRefs) {

					try {
						Catalog catalogByReference = Catalog
								.getCatalogByReference(vcloudClient,
										referenceType);
						System.out.println(referenceType.getName());

						Metadata metadata = catalogByReference.getMetadata();
						metadata.updateMetadataEntry("aaa", "aaa");

						System.out.println(metadata.getMetadataEntry("aaa"));
					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			for (ReferenceType vdcRef : Organization
					.getOrganizationByReference(vcloudClient, orgRef)
					.getVdcRefs()) {

				Vdc vdc = Vdc.getVdcByReference(vcloudClient, vdcRef);

				// base.mydata.Vdc vdc2 = new base.mydata.Vdc(vdcRef.getName(),
				// vdc, vcloudClient);
				//
				// vdc2.setMetadataInt("intval",
				// (int) System.currentTimeMillis() % 100000);

				// int metadataInt = vdc2.getMetadataInt("intval");
				// System.out.println("medadataInt" + metadataInt);

				// VDCにはメタデータは使用できない模様
				// vdc2.metadataUpdate();

			}
		}
	}
}

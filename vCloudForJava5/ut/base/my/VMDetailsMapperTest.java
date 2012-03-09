package base.my;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.HttpException;
import org.junit.BeforeClass;
import org.junit.Test;

import utconf.UTConf;
import base.mydata.VApp;
import base.mydata.VObj;

import com.vmware.vcloud.sdk.VCloudException;

/**
 *
 *
 *
 * @author user
 *
 */
public class VMDetailsMapperTest {

	private static VMDetailsMapper mapper;

	@BeforeClass
	public static void beforClass() throws Exception {
		try {
			mapper = new VMDetailsMapper(new UTConf());
			mapper.run();
		} catch (Exception e) {

			e.printStackTrace();
			fail();

		}
	}

	@Test
	public void testGetVappSet() throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException, VCloudException, HttpException, IOException {

		System.out.println("testGetVappSet --------------------------------");
		{
			Set<VApp> vappSet = mapper.getVappSet("KAIGIV5");

			for (VObj vApp : vappSet) {
				System.out.println(vApp);
			}
		}
		{

			HashMap<String, Set<VApp>> vappMap = mapper.getVappMap();

			for (String str : vappMap.keySet()) {
				Set<VApp> vappSet = vappMap.get(str);
				System.out.println("★：" + str);

				for (VObj vApp : vappSet) {
					System.out.println(vApp);
				}

			}

		}

	}

	@Test
	public void testGetVappSetByUser1() throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException, VCloudException, HttpException, IOException {
		System.out
				.println("testGetVappSetByUser[miura] --------------------------------");

		{
			Set<VApp> vappSet = mapper.getVappSetByUser("KAIGIV5", "miura");

			for (VObj vApp : vappSet) {
				System.out.println(vApp);
				assertTrue(true);
			}
			if (vappSet.size() == 0) {
				fail();
			}
		}

	}

	@Test
	public void testGetVappSetByUser2() throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException, VCloudException, HttpException, IOException {
		System.out
				.println("testGetVappSetByUser[fujita] --------------------------------");

		{
			Set<VApp> vappSet = mapper.getVappSetByUser("KAIGIV5", "fujita");

			for (VObj vApp : vappSet) {
				System.out.println(vApp);
				assertTrue(true);
			}
			if (vappSet.size() == 0) {
				fail();
			}
		}

	}

	@Test
	public void testGetVappMap() throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException, VCloudException, HttpException, IOException {

		{

			System.out
					.println("testGetVappMap --------------------------------");
			HashMap<String, Set<VApp>> vappMap = mapper.getVappMap();

			for (String str : vappMap.keySet()) {
				Set<VApp> vappSet = vappMap.get(str);
				System.out.println("★：" + str);

				for (VObj vApp : vappSet) {
					System.out.println(vApp);
				}

			}

		}

	}

}

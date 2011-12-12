package my;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Set;

import mydata.VApp;

import org.apache.http.HttpException;
import org.junit.Test;

import com.vmware.vcloud.sdk.VCloudException;

import utconf.Conf;

public class VMDetailsMapperTest {

	@Test
	public void test() throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException, VCloudException, HttpException, IOException {
		try {
			VMDetailsMapper mapper = new VMDetailsMapper(Conf.HOST, Conf.USER,
					Conf.PASS);
			mapper.run();

			{
				Set<VApp> vappSet = mapper.getVappSet("KAIGIV5");


				for (VApp vApp : vappSet) {
					System.out.println(vApp);
				}
			}

			if(false){
				HashMap<String, Set<VApp>> vappMap = mapper.getVappMap();

				for (String str : vappMap.keySet()) {
					Set<VApp> vappSet = vappMap.get(str);
					System.out.println("★：" + str);



					for (VApp vApp : vappSet) {
						System.out.println(vApp);
					}

				}

			}

		} catch (Exception e) {

			e.printStackTrace();
			fail();

		}

	}

}

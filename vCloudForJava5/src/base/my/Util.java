package base.my;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.logging.Level;

import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.samples.extra.FakeSSLSocketFactory;

public class Util {


	public static VcloudClient login(String url,String user,String pass)
			throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, VCloudException {
		return login(new String[]{url,user,pass});

	}
	/**
	 *
	 * @param args URL.user,pass
	 * @return
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws VCloudException
	 */
	public static VcloudClient login(String[] args)
			throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, VCloudException {
		VcloudClient.setLogLevel(Level.OFF);
		VcloudClient vcloudClient = new VcloudClient(args[0], Version.V1_5);
		vcloudClient.registerScheme("https", 443,
				FakeSSLSocketFactory.getInstance());
		vcloudClient.login(args[1], args[2]);
		return vcloudClient;
	}

}

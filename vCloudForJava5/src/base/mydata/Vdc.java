package base.mydata;

import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;

/**
 *
 * アプリからはみ出たデータを入れるつもりだったが、メタデータが使用できない仕様の模様。
 * よって使うな。
 *
 * @deprecated
 *
 */
@Deprecated
public class Vdc extends VObj {

	protected com.vmware.vcloud.sdk.Vdc vdc;
	protected String vcdName;

	protected VcloudClient vcloudClient;

	/**
	 * equals実装用のテンポラリフィールド
	 */
	private String _name;

	public Vdc(String vcdName, com.vmware.vcloud.sdk.Vdc vdc,
			VcloudClient vcloudClient) throws VCloudException {
		super(vdc);
		this.vdc = vdc;
		this.vcdName = vcdName;

		this.vcloudClient = vcloudClient;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vdc other = (Vdc) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		return true;
	}

}

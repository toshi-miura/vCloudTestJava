package base.mydata;

import java.util.HashMap;

import com.vmware.vcloud.sdk.Metadata;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudEntity;

public class VObj {

	protected Metadata metadata;
	/**
	 * update用のテンポラリキャッシュ
	 */
	protected HashMap<String, String> updateMap = new HashMap<String, String>();

	public VObj() {

	}

	public VObj(VcloudEntity entity) {

		super();
		try {
			metadata = entity.getMetadata();
		} catch (VCloudException e) {

			e.printStackTrace();
		}

	}

	protected void setMetadata(String k, String v) throws VCloudException {

		metadata.updateMetadataEntry(k, v);
	}

	protected HashMap<String, String> getMetadata() throws VCloudException {

		return metadata.getMetadataEntries();
	}

	public String getMetadataStr(String k) throws VCloudException {

		if (updateMap.containsKey(k)) {

			return updateMap.get(k);
		} else {
			HashMap<String, String> metadataEntries = metadata
					.getMetadataEntries();
			return metadataEntries.get(k);
		}
	}

	/**
	 * 未設定の場合は、-1を返す。
	 * @param k
	 * @return
	 * @throws VCloudException
	 */
	public int getMetadataInt(String k) throws VCloudException {
		String str = getMetadataStr(k);
		if (str != null && !str.equals("")) {
			return Integer.parseInt(str);
		} else {
			return -1;
		}
	}

	public void setMetadataStr(String k, String val) throws VCloudException {

		String entry = "";
		try {
			entry = getMetadataStr(k);
		} catch (Exception e) {
		}

		// 値が変わっていた場合のみアップデート
		if (!val.equals(entry)) {
			updateMap.put(k, val);
			// metadata.updateMetadataEntry(k, val);
		}
	}

	/**
	 * 更新対象がない場合は更新しない
	 * @throws VCloudException
	 */
	public void metadataUpdate() throws VCloudException {
		if (updateMap.size() != 0) {
			metadata.updateMetadataEntries(updateMap);

		}

	}

	public void setMetadataInt(String k, int val) throws VCloudException {

		setMetadataStr(k, Integer.toString(val));
	}

}
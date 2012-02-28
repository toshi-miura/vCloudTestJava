package base.my;

import java.util.List;

import base.mydata.VApp;

import com.vmware.vcloud.sdk.VCloudException;

public interface DeletedVappHandler {

	void handle(List<VApp> vappList) throws VCloudException;
}

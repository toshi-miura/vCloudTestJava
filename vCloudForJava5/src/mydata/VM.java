package mydata;

import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.constants.VMStatus;

/**
 * VMレベルでの管理をするか要検討。 仕様を単純化し、VAPPのレイヤーでしか対応しない方法は一考の余地あり。
 *
 * @author user
 *
 */
public class VM {

	private com.vmware.vcloud.sdk.VM vm;

	public VM(com.vmware.vcloud.sdk.VM vm) {
		super();
		this.vm = vm;



	}

	public com.vmware.vcloud.sdk.VM getRawVm() {
		return vm;
	}

	public String getName() {
		return vm.getResource().getName();
	}

	public VMStatus getStatus() {
		return vm.getVMStatus();
	}

	public int getCpu() throws VCloudException {
		return vm.getCpu().getNoOfCpus();
	}

	public int getMemorySizeMB() throws VCloudException {
		return vm.getMemory().getMemorySize().intValue();
	}

	public int getTotalHDDGB() throws VCloudException {
		int sum = 0;
		for (VirtualDisk disk : vm.getDisks()) {
			if (disk.isHardDisk()) {

				sum += disk.getHardDiskSize().intValue();
			}
		}

		return sum / 1000;
	}

	@Override
	public String toString() {

		try {
			return "VMINFO:	"+getName() + ":" + getCpu() + ":" + getMemorySizeMB() + ":"
					+ getTotalHDDGB();
		} catch (VCloudException e) {
			return e.getMessage();
		}
	}

}

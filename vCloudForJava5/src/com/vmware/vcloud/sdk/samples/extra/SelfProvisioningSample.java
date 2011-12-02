/*
 * *******************************************************
 * Copyright VMware, Inc. 2010-2011.  All Rights Reserved.
 * *******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.vmware.vcloud.sdk.samples.extra;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import com.vmware.vcloud.api.rest.schema.AdminCatalogType;
import com.vmware.vcloud.api.rest.schema.AdminOrgType;
import com.vmware.vcloud.api.rest.schema.AdminVdcType;
import com.vmware.vcloud.api.rest.schema.CapacityWithUsageType;
import com.vmware.vcloud.api.rest.schema.ComputeCapacityType;
import com.vmware.vcloud.api.rest.schema.IpRangeType;
import com.vmware.vcloud.api.rest.schema.IpRangesType;
import com.vmware.vcloud.api.rest.schema.IpScopeType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.OrgEmailSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgGeneralSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgLdapSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgNetworkType;
import com.vmware.vcloud.api.rest.schema.OrgSettingsType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminCatalog;
import com.vmware.vcloud.sdk.admin.AdminOrgNetwork;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.ProviderVdc;
import com.vmware.vcloud.sdk.admin.User;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;
import com.vmware.vcloud.sdk.admin.extensions.VcloudAdminExtension;
import com.vmware.vcloud.sdk.constants.AllocationModelType;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.LdapModeType;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * vCloud Java SDK sample that demonstrates self provisioning operations when
 * on-boarding a customer @ vCloud Service Provider. These operations includes
 * Administrative Tasks such as Create Organization, Create vDC, Add Catalog &
 * User to the Organization etc.
 *
 * @author Ecosystem Engineering
 *
 */

public class SelfProvisioningSample {

	private static VcloudClient client;
	private static VcloudAdmin adminClient;

	/**
	 * @param args
	 * @throws TimeoutException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 */
	public static void main(String[] args) throws TimeoutException,
			KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException {
		try {

			if (args.length < 3) {
				System.out
						.println("java SelfProvisioningSample vCloudURL user@organization password");
				System.out
						.println("java SelfProvisioningSample https://vcloud user@System password");
				System.exit(0);
			}

			VcloudClient.setLogLevel(Level.OFF);
			client = new VcloudClient(args[0], Version.V1_5);
			client.registerScheme("https", 443, FakeSSLSocketFactory
					.getInstance());
			client.login(args[1], args[2]);

			adminClient = client.getVcloudAdmin();

			AdminOrgType adminOrgType = new AdminOrgType();
			OrgSettingsType orgSettings = new OrgSettingsType();
			setOrgProfile(adminOrgType);
			populateOrgSettings(orgSettings);
			adminOrgType.setSettings(orgSettings);

			System.out.println("API Self Provisioning Sample");
			System.out.println("----------------------------");

			// Create organization
			System.out.println("");
			AdminOrganization adminOrg = adminClient
					.createAdminOrg(adminOrgType);
			System.out.println("Creating API Sample Org : "
					+ adminOrg.getResource().getName() + " : "
					+ adminOrg.getResource().getHref());
			List<Task> tasks = adminOrg.getTasks();
			if (tasks.size() > 0)
				tasks.get(0).waitForTask(0);

			// Create vDC You may end using one of the following.
			addVdc(adminClient, adminOrg);
			addPayAsYouGoVdc(adminClient, adminOrg);

			// Create user on the organization
			addUserToOrg(adminOrg);

			// Create catalog on the organization
			addCatalog(adminOrg);

			// Create org networks on the organizaiton
			addBridgedOrgNetwork(adminOrg);
			addIsolatedOrgNetwork(adminOrg);

		} catch (VCloudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Adding catalog to the organization
	 *
	 * @param adminOrg
	 * @throws TimeoutException
	 */
	private static void addCatalog(AdminOrganization adminOrg)
			throws TimeoutException {
		AdminCatalogType newCatalogType = new AdminCatalogType();
		try {
			// Name this Catalog
			newCatalogType.setName("API_Sample_Catalog");
			newCatalogType.setDescription("API Sample Catalog Description");

			// Share this Catalog
			newCatalogType.setIsPublished(true);

			AdminCatalog adminCatalog = adminOrg.createCatalog(newCatalogType);
			System.out.println("Creating API Sample Catalog : "
					+ adminCatalog.getResource().getName() + " : "
					+ adminCatalog.getResource().getHref());
			List<Task> tasks = adminCatalog.getTasks();
			if (tasks.size() > 0)
				tasks.get(0).waitForTask(0);

		} catch (VCloudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Adding user to the organization
	 *
	 * @param adminOrg
	 * @throws TimeoutException
	 */
	private static void addUserToOrg(AdminOrganization adminOrg)
			throws TimeoutException {
		UserType newUserType = new UserType();

		// Credentias
		newUserType.setName("API_Sample_user");
		newUserType.setPassword("password");
		newUserType.setIsEnabled(true);

		// Role : 'Organization Administrator'
		ReferenceType usrRoleRef = adminClient
				.getRoleRefByName("Organization Administrator");
		newUserType.setRole(usrRoleRef);

		// COntact Info:
		newUserType.setFullName("User Full Name");
		newUserType.setEmailAddress("user@company.com");
		// Use defaults for rest of the fields.

		try {
			User user = adminOrg.createUser(newUserType);

			System.out.println("Creating API Sample User : "
					+ user.getResource().getName() + " : "
					+ user.getResource().getHref());
			List<Task> tasks = user.getTasks();
			if (tasks.size() > 0)
				tasks.get(0).waitForTask(0);

		} catch (VCloudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adding isolated org network to the organization
	 *
	 * @param adminOrg
	 * @throws VCloudException
	 * @throws TimeoutException
	 */
	private static void addIsolatedOrgNetwork(AdminOrganization adminOrg)
			throws VCloudException, TimeoutException {

		OrgNetworkType orgNetType = new OrgNetworkType();
		orgNetType.setName("API_Sample_IsolatedOrgNet");
		orgNetType
				.setDescription("Isolated Org Network. Change the hardcoding from the Sample");

		// COnfigure Internal IP Settings
		NetworkConfigurationType netConfig = new NetworkConfigurationType();

		IpScopeType ipScope = new IpScopeType();
		ipScope.setNetmask("255.255.255.0");
		ipScope.setGateway("192.168.1.1");
		ipScope.setDns1("1.2.3.4");
		ipScope.setIsInherited(false);

		// IP Ranges
		IpRangesType ipRangesType = new IpRangesType();
		IpRangeType ipRangeType = new IpRangeType();
		ipRangeType.setStartAddress("192.168.1.100");
		ipRangeType.setEndAddress("192.168.1.199");
		ipRangesType.getIpRange().add(ipRangeType);

		ipScope.setIpRanges(ipRangesType);

		netConfig.setIpScope(ipScope);
		netConfig.setFenceMode(FenceModeValuesType.ISOLATED.value());

		VcloudAdminExtension vmwClient = client.getVcloudAdminExtension();

		// Change the network pool for creating an isolated org network
		ReferenceType netPoolRef = vmwClient.getVMWNetworkPoolRefsByName().get(
				"NetPool-VC1-VLANBACKED");

		orgNetType.setNetworkPool(netPoolRef);
		orgNetType.setConfiguration(netConfig);
		orgNetType.setAllowedExternalIpAddresses(null);

		// Now create the Isolated Org Network
		AdminOrgNetwork adminOrgNet = adminOrg.createOrgNetwork(orgNetType);

		System.out.println("Creating API Sample Isolated Org Network : "
				+ adminOrgNet.getResource().getName() + " : "
				+ adminOrgNet.getResource().getHref());
		List<Task> tasks = adminOrgNet.getTasks();
		if (tasks.size() > 0)
			tasks.get(0).waitForTask(0);

	}

	/**
	 * Adding an org network (Bridged) backed by the external network
	 *
	 * @param adminOrg
	 * @throws VCloudException
	 * @throws TimeoutException
	 */
	private static void addBridgedOrgNetwork(AdminOrganization adminOrg)
			throws VCloudException, TimeoutException {

		OrgNetworkType orgNetType = new OrgNetworkType();

		// change the external network name
		NetworkConfigurationType netConfig = new NetworkConfigurationType();
		ReferenceType extNetRef = adminClient
				.getExternalNetworkRefByName("External Network - VC1");
		netConfig.setParentNetwork(extNetRef);
		netConfig.setFenceMode(FenceModeValuesType.BRIDGED.value());

		orgNetType.setName("API_Sample_BridgedOrgNet");
		orgNetType
				.setDescription("Bridged Org Network. Change the hardcoding from the Sample");
		orgNetType.setConfiguration(netConfig);

		// Now create the Bridged Org Network
		AdminOrgNetwork adminOrgNet = adminOrg.createOrgNetwork(orgNetType);

		System.out.println("Creating API Sample Bridged Org Network: "
				+ adminOrgNet.getResource().getName() + " : "
				+ adminOrgNet.getResource().getHref());
		List<Task> tasks = adminOrgNet.getTasks();
		if (tasks.size() > 0)
			tasks.get(0).waitForTask(0);

	}

	/**
	 * Adding the pay as you go vdc.
	 *
	 * @param adminClient
	 * @param adminOrg
	 * @throws VCloudException
	 * @throws TimeoutException
	 */
	private static void addPayAsYouGoVdc(VcloudAdmin adminClient,
			AdminOrganization adminOrg) throws VCloudException,
			TimeoutException {
		AdminVdcType adminVdcType = new AdminVdcType();

		// Select Provider VDC
		ReferenceType pvdcRef = adminClient.getProviderVdcRefByName("PvDC-VC1");
		adminVdcType.setProviderVdcReference(pvdcRef);
		ProviderVdc pvdc = ProviderVdc.getProviderVdcByReference(client,
				pvdcRef);

		// Select Allocation Model - 'Pay As You Go' Model
		adminVdcType.setAllocationModel(AllocationModelType.ALLOCATIONVAPP
				.value());

		adminVdcType.setResourceGuaranteedCpu(0.25); // 25% CPU Resources
		// guaranteed
		adminVdcType.setResourceGuaranteedMemory(0.99); // 99% Memory resources
		// guaranteed
		// Rest all Defaults for the 'Pay As You Go Model' configuration.

		// Allocate Storage
		CapacityWithUsageType storageCapacity = new CapacityWithUsageType();
		storageCapacity.setUnits("MB");
		storageCapacity.setAllocated(new Long(10240));
		storageCapacity.setLimit(new Long(10240));
		adminVdcType.setStorageCapacity(storageCapacity);

		// COmpute Capacity -- this is needed. UI Uses defaults.
		ComputeCapacityType computeCapacity = new ComputeCapacityType();
		CapacityWithUsageType cpu = new CapacityWithUsageType();
		cpu.setAllocated(Long.parseLong("10"));
		cpu.setOverhead(Long.parseLong("0"));
		cpu.setUnits("MHz");
		cpu.setUsed(Long.parseLong("0"));
		cpu.setLimit(Long.parseLong("0"));

		computeCapacity.setCpu(cpu);

		CapacityWithUsageType mem = new CapacityWithUsageType();
		mem.setAllocated(Long.parseLong("10"));
		mem.setOverhead(Long.parseLong("0"));
		mem.setUnits("MB");
		mem.setUsed(Long.parseLong("0"));
		mem.setLimit(Long.parseLong("1000"));

		computeCapacity.setMemory(mem);

		adminVdcType.setComputeCapacity(computeCapacity);

		// Select Network Pool
		ReferenceType netPoolRef = pvdc
				.getVMWNetworkPoolRefByName("NetPool-VC1-VLANBACKED");
		adminVdcType.setNetworkPoolReference(netPoolRef);
		adminVdcType.setNetworkQuota(24);

		// Name this Organization vDC
		adminVdcType.setName("API_Sample_Pay_as_you_go_vdc");
		adminVdcType.setDescription("API Sample - Pay as you go vdc");
		adminVdcType.setIsEnabled(true);

		AdminVdc adminVdc = adminOrg.createAdminVdc(adminVdcType);

		System.out.println("Creating API Sample Pay As You Go Vdc : "
				+ adminVdc.getResource().getName() + " : "
				+ adminVdc.getResource().getHref());
		List<Task> tasks = adminVdc.getTasks();
		if (tasks.size() > 0)
			tasks.get(0).waitForTask(0);

	}

	/**
	 * Adding the allocation pool vdc
	 *
	 * @param adminClient
	 * @param adminOrg
	 * @throws VCloudException
	 * @throws TimeoutException
	 */
	private static void addVdc(VcloudAdmin adminClient,
			AdminOrganization adminOrg) throws VCloudException,
			TimeoutException {
		AdminVdcType adminVdcType = new AdminVdcType();
		adminVdcType.setName("API_Sample_Allocation_Pool_vdc");
		adminVdcType.setIsEnabled(true);

		adminVdcType.setResourceGuaranteedCpu(1.00);
		adminVdcType.setResourceGuaranteedMemory(1.00);

		adminVdcType.setVmQuota(25);
		adminVdcType
				.setDescription("API Sample Test - Self provisioning using API");

		// Change the provider vdc name. The to be Creating vdc will be backed
		// by
		// this provider vdc.
		ReferenceType pvdcRef = adminClient.getProviderVdcRefByName("PvDC-VC1");
		ProviderVdc pvdc = ProviderVdc.getProviderVdcByReference(client,
				pvdcRef);
		adminVdcType.setProviderVdcReference(pvdcRef);

		// Change the type of provider vdc
		adminVdcType.setAllocationModel(AllocationModelType.ALLOCATIONPOOL
				.value());

		// Change the compute capacities cpu, memory
		ComputeCapacityType computeCapacity = new ComputeCapacityType();
		CapacityWithUsageType cpu = new CapacityWithUsageType();
		cpu.setAllocated(Long.parseLong("10"));
		cpu.setOverhead(Long.parseLong("0"));
		cpu.setUnits("MHz");
		cpu.setUsed(Long.parseLong("0"));
		cpu.setLimit(Long.parseLong("0"));

		computeCapacity.setCpu(cpu);

		CapacityWithUsageType mem = new CapacityWithUsageType();
		mem.setAllocated(Long.parseLong("10"));
		mem.setOverhead(Long.parseLong("0"));
		mem.setUnits("MB");
		mem.setUsed(Long.parseLong("0"));
		mem.setLimit(Long.parseLong("1000"));

		computeCapacity.setMemory(mem);

		adminVdcType.setComputeCapacity(computeCapacity);

		// Change the storage capacites.
		CapacityWithUsageType storageCapacity = new CapacityWithUsageType();
		storageCapacity.setAllocated(Long.parseLong("123456"));
		storageCapacity.setOverhead(Long.parseLong("0"));
		storageCapacity.setUnits("MB");
		storageCapacity.setUsed(Long.parseLong("0"));
		storageCapacity.setLimit(Long.parseLong("123456"));

		adminVdcType.setStorageCapacity(storageCapacity);

		// Change the network pool reference
		ReferenceType netPoolRef = pvdc
				.getVMWNetworkPoolRefByName("NetPool-VC1-VLANBACKED");

		adminVdcType.setNetworkPoolReference(netPoolRef);
		adminVdcType.setNetworkQuota(1024);

		AdminVdc adminVdc = adminOrg.createAdminVdc(adminVdcType);

		System.out.println("Creating API Sample Allocation Pool Vdc : "
				+ adminVdc.getResource().getName() + " : "
				+ adminVdc.getResource().getHref());
		List<Task> tasks = adminVdc.getTasks();
		if (tasks.size() > 0)
			tasks.get(0).waitForTask(0);

	}

	/**
	 * Populating the organization settings.
	 *
	 * @param orgSettings
	 */
	private static void populateOrgSettings(OrgSettingsType orgSettings) {

		OrgGeneralSettingsType orgGeneralSettingsType = new OrgGeneralSettingsType();
		// LDAP Options
		OrgLdapSettingsType orgLdapSettingsType = new OrgLdapSettingsType();
		orgLdapSettingsType.setOrgLdapMode(LdapModeType.NONE.value());

		// Add Local Users
		// None at this time

		// Catalog Publishing
		orgGeneralSettingsType.setCanPublishCatalogs(true);

		// Email Preferences
		OrgEmailSettingsType orgEmailSettings = new OrgEmailSettingsType();
		orgEmailSettings.setIsDefaultSmtpServer(true);
		orgEmailSettings.setFromEmailAddress("user@company.com");
		orgEmailSettings.setDefaultSubjectPrefix("Java SDK API Sample");
		orgEmailSettings.getAlertEmailTo().add("user@company.com");
		orgSettings.setOrgEmailSettings(orgEmailSettings);

		// Policies
		orgGeneralSettingsType.setDeployedVMQuota(100);
		orgGeneralSettingsType.setStoredVmQuota(0);
		orgSettings.setOrgGeneralSettings(orgGeneralSettingsType);
		orgSettings.setOrgLdapSettings(orgLdapSettingsType);
		OrgLeaseSettingsType orgLeaseSettings = new OrgLeaseSettingsType();
		orgLeaseSettings.setDeploymentLeaseSeconds(86400);
		orgLeaseSettings.setStorageLeaseSeconds(864000);
		orgSettings.setVAppLeaseSettings(orgLeaseSettings);

	}

	/**
	 * Setting the Organization name, description and the fullname
	 *
	 * @param adminOrgType
	 */
	private static void setOrgProfile(AdminOrgType adminOrgType) {
		adminOrgType.setFullName("API Sample Org");
		adminOrgType.setDescription("API Sample - Self provisioning using API");
		adminOrgType.setName("API_Sample_Org");
		adminOrgType.setIsEnabled(true);
	}

}

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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.apache.http.HttpException;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.admin.User;
import com.vmware.vcloud.sdk.constants.Version;

/*
 * This sample lists all the vdc's (Name, Allocation Model), vapp's (Name) and its vms (Name, Status, Cpu, Memory & HardDisks).
 *
 */

public class VMDetails {

	public static void main(String args[]) throws HttpException,
			VCloudException, IOException, KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException,
			KeyStoreException {

		if (args.length < 3) {
			System.out
					.println("java VMDetails vCloudURL user@organization password");
			System.out
					.println("java VMDetails https://vcloud user@System password");
			System.exit(0);
		}

		// Client login
		VcloudClient.setLogLevel(Level.OFF);
		VcloudClient vcloudClient = new VcloudClient(args[0], Version.V1_5);
		vcloudClient.registerScheme("https", 443,
				FakeSSLSocketFactory.getInstance());
		vcloudClient.login(args[1], args[2]);
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

					System.out.println("	Vapp : " + vAppRef.getName());

					// ------------
					System.out.println("	Vapp_OtherAttributes : "
							+ vAppRef.getOtherAttributes().size());
					System.out.println("	Vapp_VCloudExtension : "
							+ vAppRef.getVCloudExtension().size());
					// ------------

					// ------------
					ReferenceType owner = Vapp.getOwner(vcloudClient, vAppRef);
					System.out.println(owner);

					try {
						User user = User
								.getUserByReference(vcloudClient, owner);

						UserType resource = user.getResource();

						System.out.println("★" + resource.getEmailAddress());
						System.out.println("★" + resource.getFullName());
					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}

					/**
					 * AccessSettingsType settings = controlAccess
					 * .getAccessSettings();
					 * 
					 * if (settings != null) {
					 * 
					 * for (AccessSettingType set : settings
					 * .getAccessSetting()) { AccessLevelType accessLevel =
					 * set.getAccessLevel();
					 * 
					 * 
					 * System.out.println("	Vapp_AccessLevel : " +
					 * accessLevel.name() + "\t" + set.getSubject().getHref());
					 * User user = User.getUserByReference(vcloudClient,
					 * set.getSubject());
					 * System.out.println("	Vapp_UserInfoMail : " +
					 * user.getResource().getEmailAddress());
					 * System.out.println("	Vapp_UserInfo : " +
					 * user.getResource().getName()); } }else{
					 * System.out.println("	Vapp_ACC_INFO_NULL : "); }
					 */

					Vapp vapp = Vapp.getVappByReference(vcloudClient, vAppRef);
					List<VM> vms = vapp.getChildrenVms();
					for (VM vm : vms) {
						System.out.println("		Vm : "
								+ vm.getResource().getName());
						System.out.println("			Status : " + vm.getVMStatus());
						System.out.println("			CPU : "
								+ vm.getCpu().getNoOfCpus());
						System.out.println("			Memory : "
								+ vm.getMemory().getMemorySize() + " Mb");
						for (VirtualDisk disk : vm.getDisks()) {
							if (disk.isHardDisk())
								System.out.println("			HardDisk : "
										+ disk.getHardDiskSize() + " Mb");
						}

					}

				}

			}
		}

	}
}

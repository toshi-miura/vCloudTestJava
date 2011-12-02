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
import java.util.logging.Level;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * This sample finds the vapp details given any one of the vm's ip
 *
 * @author Ecosystem Engineering
 *
 */

public class FindvAppByIp {

	static VcloudClient client;

	/**
	 * This method finds the vapp for the given vm's ip.
	 *
	 * @param expectedIp
	 * @return
	 * @throws VCloudException
	 */
	public static ReferenceType findvAppByIp(String expectedIp)
			throws VCloudException {
		for (ReferenceType orgRef : client.getOrgRefsByName().values()) {
			Organization org = Organization.getOrganizationByReference(client,
					orgRef);
			for (ReferenceType vdcRef : org.getVdcRefs()) {
				Vdc vdc = Vdc.getVdcByReference(client, vdcRef);
				for (ReferenceType vappRef : vdc.getVappRefs()) {
					Vapp vapp = Vapp.getVappByReference(client, vappRef);
					for (VM vm : vapp.getChildrenVms()) {
						for (String actualIp : vm.getIpAddressesById().values()) {
							if (actualIp != null)
								if (actualIp.equals(expectedIp)) {
									return vm.getParentVappReference();
								}
						}
					}
				}
			}
		}
		return null;

	}

	public static void main(String args[]) throws VCloudException,
			KeyManagementException, NoSuchAlgorithmException, IOException,
			UnrecoverableKeyException, KeyStoreException {

		if (args.length < 4) {
			System.out
					.println("java FindvAppByIp vCloudURL user@organization password ipaddress");
			System.out
					.println("java FindvAppByIp https://vcloud user@Organizaiton password ipaddress");
			System.exit(0);
		}

		VcloudClient.setLogLevel(Level.OFF);
		client = new VcloudClient(args[0], Version.V1_5);
		client.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
		client.login(args[1], args[2]);

		ReferenceType vappRef = findvAppByIp(args[3]);

		Vapp vapp = Vapp.getVappByReference(client, vappRef);
		System.out.println(vapp.getResource().getName());
		System.out.println(vapp.getResource().getHref());
	}
}

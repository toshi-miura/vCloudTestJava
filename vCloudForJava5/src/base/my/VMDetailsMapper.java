package base.my;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import base.mydata.VApp;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.inject.Inject;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;

/**
 * TODO
 * <li>耐久試験（接続維持時間）を調べる</li>
 * 今のモデルだと、マシン消えない
 *
 *
 */
public class VMDetailsMapper {

	private static Logger log = LoggerFactory.getLogger(VMDetailsMapper.class);

	private final VcloudClient vcloudClient;

	private final HashMap<String, Set<VApp>> vappMap = new HashMap<String, Set<VApp>>();
	private final HashMap<String, Set<VApp>> oldMap = new HashMap<String, Set<VApp>>();

	private DeletedVappHandler handler;

	public DeletedVappHandler getHandler() {
		return handler;
	}

	@Inject
	public void setHandler(DeletedVappHandler handler) {
		this.handler = handler;
	}

	public VMDetailsMapper(VcloudClient vcloudClient) {

		this.vcloudClient = vcloudClient;
	}

	@Inject
	public VMDetailsMapper(VcdAccConf conf) throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException, VCloudException {
		this(conf.HOST, conf.USER, conf.PASS);
	}

	public VMDetailsMapper(String url, String user, String pass)
			throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, VCloudException {
		this.vcloudClient = Util.login(url, user, pass);
	}

	public synchronized Set<String> getVCDNameSet() {
		return vappMap.keySet();

	}

	public synchronized void run() throws VCloudException {

		log.info("run with {}", getHandler());

		oldMap.putAll(vappMap);
		vappMap.clear();

		HashMap<String, ReferenceType> orgsList = vcloudClient
				.getOrgRefsByName();
		for (ReferenceType orgRef : orgsList.values()) {
			for (ReferenceType vdcRef : Organization
					.getOrganizationByReference(vcloudClient, orgRef)
					.getVdcRefs()) {

				Vdc vdc = Vdc.getVdcByReference(vcloudClient, vdcRef);

				for (ReferenceType vAppRef : Vdc.getVdcByReference(
						vcloudClient, vdcRef).getVappRefs()) {

					VApp vApp = mapVApp(vdcRef.getName(), vAppRef);

					put(vdcRef.getName(), vApp);

				}

			}
		}

		diff();

	}

	/**
	 * 削除されたVAPPの抽出。
	 * 新規作成は無視する
	 * @throws VCloudException
	 */
	public void diff() throws VCloudException {

		log.info("diff---------------------------------------------");

		for (String vcdname : this.vappMap.keySet()) {

			Set<VApp> olds = oldMap.get(vcdname);
			Set<VApp> vapps = vappMap.get(vcdname);

			if (olds != null && vapps != null) {
				log.info("{OLD:{}     VAPP:{}}", new Object[] { olds.size(),
						vapps.size() });

				SetView<VApp> difference = Sets.difference(olds, vapps);

				log.info("checkDiff [{}]  DIFFCOUNT:{}", new Object[] {
						vcdname, difference.size() });

				List<VApp> deletedVapp = new ArrayList<>();
				for (VApp vApp : difference) {

					if (oldMap.get(vcdname).contains(vApp)) {
						log.info("■REMOVE ?? {}", vApp.toBaseString());
						deletedVapp.add(vApp);
					}

					if (vappMap.get(vcdname).contains(vApp)) {
						log.info("■NEW ?? {}", vApp.toBaseString());
					}

				}

				if (handler != null) {

					if (deletedVapp.size() > 0) {
						log.info("run deletedHandler");
						this.handler.handle(deletedVapp);
					} else {
						log.info("Don't have deletedVapp....");
					}
				} else {
					log.info("Don't have deletedHandler....");
				}

			}

		}

		log.info("diff---------------------------------------------");

	}

	public synchronized VApp refresh(VApp vapp) throws VCloudException {

		Vapp vcdVapp = vapp.getVcdVapp();

		ReferenceType reference = vcdVapp.getReference();
		VApp newApp = mapVApp(vapp.getVcdName(), reference);
		synchronized (this) {

			vappMap.get(vapp.getVcdName()).remove(vapp);
			vappMap.get(newApp.getVcdName()).add(newApp);

		}
		return newApp;
	}

	public synchronized Set<VApp> refresh(Set<? extends VApp> vappSet)
			throws VCloudException {

		Set<VApp> result = new HashSet<VApp>();
		for (VApp vApp2 : vappSet) {
			result.add(refresh(vApp2));
		}
		return result;

	}

	private synchronized void put(String vcdName, VApp app) {
		Set<VApp> set = vappMap.get(vcdName);
		if (set == null) {
			set = new HashSet<VApp>();
			vappMap.put(vcdName, set);

		}
		set.add(app);

	}

	private VApp mapVApp(String vcdName, ReferenceType vAppRef)
			throws VCloudException {

		/*
		 * log.info("	Vapp_OtherAttributes : " +
		 * vAppRef.getOtherAttributes().size());
		 * log.info("	Vapp_VCloudExtension : " +
		 * vAppRef.getVCloudExtension().size());
		 */

		// vcloudClient.getVcloudAdmin()ext.
		// vcloudClient.getVcloudAdminExtension().

		// TODO
		System.out.println(vAppRef.getHref());
		Vapp vapp = Vapp.getVappByReference(vcloudClient, vAppRef);

		VApp app = new VApp(vcdName, vAppRef, vapp, vcloudClient);

		return app;

	}

	public synchronized HashMap<String, Set<VApp>> getVappMap() {
		return vappMap;
	}

	public synchronized Set<VApp> getVappSet(String vcdNamd) {

		log.info(vcdNamd);
		log.info("havaVcd {} ", Joiner.on(",").join(vappMap.keySet()));
		for (Entry<String, Set<VApp>> e : vappMap.entrySet()) {
			log.info(" VCDNAME {} vappNo {}", e.getKey(), e.getValue().size());
		}

		return new HashSet<VApp>(vappMap.get(vcdNamd));
	}

	/**
	 * 当面は、１データセンターしか想定しないので必要ない
	 *
	 * @param userid
	 * @return
	 */
	public synchronized HashMap<String, Set<VApp>> getVappSetByUser(
			String userid) {

		throw new IllegalStateException("未実装。呼ぶな");

	}

	/**
	 *
	 */
	public synchronized Set<VApp> getVappSetByUser(String vcdNamd, String userid) {
		Set<VApp> vappSet = getVappSet(vcdNamd);
		Set<VApp> resultSet = new HashSet<VApp>();

		resultSet.addAll(filterOnwer(vappSet, userid));
		resultSet.addAll(filterUser(vappSet, userid));

		return resultSet;

	}

	public synchronized Set<VApp> getVappSetByName(String vcdNamd,
			String userid, String vAppName) throws VCloudException {
		Set<VApp> vappSet = getVappSet(vcdNamd);
		Set<VApp> tempSet = new HashSet<VApp>();

		tempSet.addAll(filterOnwer(vappSet, userid));
		tempSet.addAll(filterUser(vappSet, userid));

		return filterVappName(vappSet, vAppName);

	}

	/**
	 * 指定されたユーザがオーナのvAPPを返す。
	 * @param vappSet
	 * @param userid
	 * @return
	 */
	private Set<VApp> filterOnwer(Set<VApp> vappSet, String userid) {
		Set<VApp> resultSet = new HashSet<VApp>();
		for (VApp vApp : vappSet) {
			if (userid.equals(vApp.getOwner().getNameInSource())) {
				resultSet.add(vApp);
			}
		}
		return resultSet;

	}

	/**
	 * 指定されたユーザがオーナのvAPPを返す。
	 * @param vappSet
	 * @param userid
	 * @return
	 * @throws VCloudException
	 */
	private Set<VApp> filterVappName(Set<VApp> vappSet, String vappName)
			throws VCloudException {
		Set<VApp> resultSet = new HashSet<VApp>();
		for (VApp vApp : vappSet) {
			if (vApp.getName().startsWith(vappName)) {
				resultSet.add(vApp);
			}
		}
		return resultSet;

	}

	/**
	 * 指定されたユーザがアクセス可能なvAPPを返す。
	 * @param vappSet
	 * @param userid
	 * @return
	 */
	private Set<VApp> filterUser(Set<VApp> vappSet, String userid) {
		Set<VApp> resultSet = new HashSet<VApp>();
		for (VApp vApp : vappSet) {
			if (userid.equals(vApp.getOwner().getNameInSource())) {
				resultSet.add(vApp);
			}
		}
		return resultSet;
	}

}

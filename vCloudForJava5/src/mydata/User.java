package mydata;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.vmware.vcloud.api.rest.schema.GroupsListType;
import com.vmware.vcloud.api.rest.schema.LinkType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.api.rest.schema.VCloudExtensionType;

/**
 * TODO
 * 権限の概念を管理すべきか・・？
 * 元のモデルでは、別オブジェクトで管理していたが、
 * 管理したくない。
 *
 * @author user
 *
 */
public class User {
	private final UserType vcdUserType;

	public final static User VCD_MASTER = new User();

	/**
	 * 通常は使わない
	 */
	private User() {
		UserType dummy = new UserType();
		this.vcdUserType = dummy;

		dummy.setId("DUMMY_ID");
		dummy.setName("DUMMY_NAME");
		dummy.setFullName("DUMMY_FULENAME");
		dummy.setAlertEmail("dummy.AlertEmail@gmail.com");
		dummy.setEmailAddress("dummy.EmailAddress@gmail.com");
		dummy.setTelephone("000-0000-0000");
	}

	public User(UserType vcdUserType) {
		this.vcdUserType = vcdUserType;
	}

	public String getAlertEmail() {
		return vcdUserType.getAlertEmail();
	}

	public String getAlertEmailPrefix() {
		return vcdUserType.getAlertEmailPrefix();
	}

	public Integer getDeployedVmQuota() {
		return vcdUserType.getDeployedVmQuota();
	}

	public String getDescription() {
		return vcdUserType.getDescription();
	}

	public String getEmailAddress() {
		return vcdUserType.getEmailAddress();
	}

	public String getFullName() {
		return vcdUserType.getFullName();
	}

	public GroupsListType getGroupReferences() {
		return vcdUserType.getGroupReferences();
	}

	public String getHref() {
		return vcdUserType.getHref();
	}

	public String getIM() {
		return vcdUserType.getIM();
	}

	public String getId() {
		return vcdUserType.getId();
	}

	public List<LinkType> getLink() {
		return vcdUserType.getLink();
	}

	public String getNameInSource() {
		return vcdUserType.getNameInSource();
	}

	public Map<QName, String> getOtherAttributes() {
		return vcdUserType.getOtherAttributes();
	}

	public String getPassword() {
		return vcdUserType.getPassword();
	}

	public ReferenceType getRole() {
		return vcdUserType.getRole();
	}

	public Integer getStoredVmQuota() {
		return vcdUserType.getStoredVmQuota();
	}

	public TasksInProgressType getTasks() {
		return vcdUserType.getTasks();
	}

	public String getTelephone() {
		return vcdUserType.getTelephone();
	}

	public String getType() {
		return vcdUserType.getType();
	}

	public List<VCloudExtensionType> getVCloudExtension() {
		return vcdUserType.getVCloudExtension();
	}

	@Override
	public int hashCode() {
		return vcdUserType.hashCode();
	}

	public Boolean isIsAlertEnabled() {
		return vcdUserType.isIsAlertEnabled();
	}

	public Boolean isIsDefaultCached() {
		return vcdUserType.isIsDefaultCached();
	}

	public Boolean isIsEnabled() {
		return vcdUserType.isIsEnabled();
	}

	public Boolean isIsExternal() {
		return vcdUserType.isIsExternal();
	}

	public Boolean isIsGroupRole() {
		return vcdUserType.isIsGroupRole();
	}

	public Boolean isIsLocked() {
		return vcdUserType.isIsLocked();
	}

	public void setAlertEmail(String value) {
		vcdUserType.setAlertEmail(value);
	}

	public void setAlertEmailPrefix(String value) {
		vcdUserType.setAlertEmailPrefix(value);
	}

	public void setDeployedVmQuota(Integer value) {
		vcdUserType.setDeployedVmQuota(value);
	}

	public void setDescription(String value) {
		vcdUserType.setDescription(value);
	}

	public void setEmailAddress(String value) {
		vcdUserType.setEmailAddress(value);
	}

	public void setFullName(String value) {
		vcdUserType.setFullName(value);
	}

	public void setGroupReferences(GroupsListType value) {
		vcdUserType.setGroupReferences(value);
	}

	public void setHref(String value) {
		vcdUserType.setHref(value);
	}

	public void setIM(String value) {
		vcdUserType.setIM(value);
	}

	public void setId(String value) {
		vcdUserType.setId(value);
	}

	public void setIsAlertEnabled(Boolean value) {
		vcdUserType.setIsAlertEnabled(value);
	}

	public void setIsDefaultCached(Boolean value) {
		vcdUserType.setIsDefaultCached(value);
	}

	public void setIsEnabled(Boolean value) {
		vcdUserType.setIsEnabled(value);
	}

	public void setIsExternal(Boolean value) {
		vcdUserType.setIsExternal(value);
	}

	public void setIsGroupRole(Boolean value) {
		vcdUserType.setIsGroupRole(value);
	}

	public void setIsLocked(Boolean value) {
		vcdUserType.setIsLocked(value);
	}

	public void setNameInSource(String value) {
		vcdUserType.setNameInSource(value);
	}

	public void setPassword(String value) {
		vcdUserType.setPassword(value);
	}

	public void setRole(ReferenceType value) {
		vcdUserType.setRole(value);
	}

	public void setStoredVmQuota(Integer value) {
		vcdUserType.setStoredVmQuota(value);
	}

	public void setTasks(TasksInProgressType value) {
		vcdUserType.setTasks(value);
	}

	public void setTelephone(String value) {
		vcdUserType.setTelephone(value);
	}

	public void setType(String value) {
		vcdUserType.setType(value);
	}

	@Override
	public String toString() {

		return getNameInSource() + "\t" + getEmailAddress() + "\t"
				+ getFullName() + "\t" + getTelephone();
	}

}

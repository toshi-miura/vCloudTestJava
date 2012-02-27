package base.dao;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Version;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class VcdUser {

	/**
	 * PK。一意
	 */
	@Id
	public String userId;

	/**
	 * VAPP名は削除したものに対し、一意ではない！！。
	 * よって、削除時にVAPPIDを決める
	 */
	public String vappID;

	@Version
	public Integer versionNo;

}

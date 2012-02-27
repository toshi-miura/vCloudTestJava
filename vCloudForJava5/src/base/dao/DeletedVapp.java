package base.dao;

import java.util.Date;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Version;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class DeletedVapp {

	/**
	 * PK。一意。
	 * VAPP名は削除したものに対し、一意ではない！！。
	 * よって、削除時にVAPPIDを決める。
	 * もしくは、。。
	 */
	@Id
	public String vappID;

	/**
	 * VAPP名
	 */
	public String vappName;

	/**
	 * VAPP名
	 */
	public Date deleteDate;

	/**
	 *
	 */
	public int cpu;

	/**
	 *
	 */
	public int memorySizeMB;

	/**
	 *
	 */
	public int totalHDDGB;

	/**
	 *
	 */
	public int lastMaxCost;

	/**
	 *
	 */
	public String auther1;

	/**
	 *
	 */
	public String auther2;

	/**
	 *
	 */
	@Version
	public Integer versionNo;

}

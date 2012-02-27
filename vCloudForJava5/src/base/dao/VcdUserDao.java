package base.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

@Dao(config = AppConfig.class)
public interface VcdUserDao {

	@Select
	VcdUser selectById(Integer userId);

	@Update
	int update(VcdUser employee);

	@Insert
	int insert(VcdUser employee);

	@Delete
	int delete(VcdUser employee);

	@Delete(sqlFile = true)
	int deleteALL();

}

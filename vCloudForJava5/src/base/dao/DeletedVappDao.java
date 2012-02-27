package base.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

@Dao(config = AppConfig.class)
public interface DeletedVappDao {

	@Select
	List<DeletedVapp> selectByUserID(String userID);

	@Select
	DeletedVapp selectByID(String vappID);

	@Update
	int update(DeletedVapp employee);

	@Insert
	int insert(DeletedVapp employee);

	@Delete
	int delete(DeletedVapp employee);

	@Delete(sqlFile = true)
	int deleteALL();

}

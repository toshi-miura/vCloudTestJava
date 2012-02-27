package base.dao;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.seasar.doma.jdbc.tx.LocalTransaction;


public class DeletedVappDaoTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		LocalTransaction tx = AppConfig.getLocalTransaction();
		try {
			// トランザクションの開始
			tx.begin();

			// aptで生成されたDaoの実装クラスを生成
			DeletedVappDao dao = new DeletedVappDaoImpl();

			// 主キーでエンティティを検索
			DeletedVapp deletedVapp = new DeletedVapp();

			// エンティティのプロパティを変更
			deletedVapp.vappID = "VAPP_ID_個体識別_" + System.currentTimeMillis();
			deletedVapp.vappName = "VAPP_NAME_名称_" + System.currentTimeMillis();

			deletedVapp.cpu = 1;
			deletedVapp.memorySizeMB = 1024;
			deletedVapp.totalHDDGB = 50;
			deletedVapp.auther1 = "li2053";
			deletedVapp.auther2 = "li2054";
			deletedVapp.deleteDate = new Date();
			deletedVapp.lastMaxCost = 3000;

			dao.insert(deletedVapp);
			// エンティティを更新

			deletedVapp.cpu = 4;
			deletedVapp.memorySizeMB = 1024;
			deletedVapp.totalHDDGB = 1000;

			dao.update(deletedVapp);

			// トランザクションのコミット

			tx.commit();
		} finally {
			// トランザクションのロールバック
			tx.rollback();
		}
	}

}

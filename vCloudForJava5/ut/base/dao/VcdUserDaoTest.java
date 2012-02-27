package base.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.seasar.doma.jdbc.tx.LocalTransaction;


public class VcdUserDaoTest {

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
			VcdUserDao dao = new VcdUserDaoImpl();

			dao.deleteALL();

			// 主キーでエンティティを検索
			VcdUser employee = new VcdUser();

			// エンティティのプロパティを変更
			employee.userId = "KING";
			employee.vappID = "aaaaaa";

			dao.insert(employee);
			dao.delete(employee);

			employee = new VcdUser();
			employee.userId = "KING";
			employee.vappID = "bbbbbb";

			dao.insert(employee);
			dao.delete(employee);

			// トランザクションのコミット
			tx.commit();
		} finally {
			// トランザクションのロールバック
			tx.rollback();
		}
	}

	/**
	 * @throws InterruptedException
	 *
	 */
	@Test
	public void test2() throws InterruptedException {
		LocalTransaction tx = AppConfig.getLocalTransaction();
		try {
			// トランザクションの開始
			tx.begin();

			// aptで生成されたDaoの実装クラスを生成
			VcdUserDao dao = new VcdUserDaoImpl();
			dao.deleteALL();

			// 主キーでエンティティを検索
			VcdUser employee1 = new VcdUser();

			// エンティティのプロパティを変更
			employee1.userId = "KING_AAA";
			employee1.vappID = "aaaaaa";

			dao.insert(employee1);

			VcdUser employee2 = new VcdUser();
			employee2.userId = "KING_BBB";
			employee2.vappID = "bbbbbb";

			dao.insert(employee2);

			// -------------------------------------------------
			DeletedVappDao dao2 = new DeletedVappDaoImpl();
			dao2.deleteALL();

			tx.commit();
			tx.begin();

			DeletedVapp deletedVapp = new DeletedVapp();

			deletedVapp.vappID = employee1.vappID;
			deletedVapp.vappName = "VAPP_NAME_名称_" + System.currentTimeMillis();

			deletedVapp.cpu = 1;
			deletedVapp.memorySizeMB = 1024;
			deletedVapp.totalHDDGB = 50;
			deletedVapp.auther1 = "li2053";
			deletedVapp.auther2 = "li2054";
			deletedVapp.deleteDate = new Date();
			deletedVapp.lastMaxCost = 3000;

			dao2.insert(deletedVapp);

			Thread.sleep(10);

			DeletedVapp deletedVapp2 = new DeletedVapp();

			deletedVapp2.vappID = employee2.vappID;
			deletedVapp2.vappName = "VAPP_NAME_名称_"
					+ System.currentTimeMillis();

			deletedVapp2.cpu = 1;
			deletedVapp2.memorySizeMB = 2048;
			deletedVapp2.totalHDDGB = 50;
			deletedVapp2.auther1 = "li2053";
			deletedVapp2.auther2 = "li2054";
			deletedVapp2.deleteDate = new Date();
			deletedVapp2.lastMaxCost = 3000;

			dao2.insert(deletedVapp2);

			// トランザクションのコミット
			tx.commit();
			tx.begin();

			List<DeletedVapp> selectByUserID = dao2
					.selectByUserID(employee1.userId);

			assertEquals(1, selectByUserID.size());
			assertEquals(1024, selectByUserID.get(0).memorySizeMB);

			List<DeletedVapp> selectByUserID2 = dao2
					.selectByUserID(deletedVapp.auther1);
			assertEquals(2, selectByUserID2.size());
			System.out.println(selectByUserID2.get(0).memorySizeMB + ":"
					+ selectByUserID2.get(0).vappID);
			System.out.println(selectByUserID2.get(1).memorySizeMB + ":"
					+ selectByUserID2.get(1).vappID);

			// 最近削除したものが上にくる
			assertEquals(2048, selectByUserID2.get(0).memorySizeMB);
			assertEquals(1024, selectByUserID2.get(1).memorySizeMB);

			dao.delete(employee1);
			dao.delete(employee2);

			dao2.delete(deletedVapp);
			dao2.delete(deletedVapp2);

			tx.commit();

		} finally {
			// トランザクションのロールバック
			tx.rollback();
		}
	}
}

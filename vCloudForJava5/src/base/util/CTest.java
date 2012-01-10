package base.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CTest {

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
	public void testFilterList() {

		List<String> l = new ArrayList<String>();
		l.add("aaa");
		l.add("aab");
		l.add("bbbb");
		l.add("ccc");
		l.add("   ");
		l.add("XXX");

		{
			List<String> list = $C.filter(l, new $C.Filter<String>() {
				@Override
				public boolean $(String v) {
					return v.startsWith("a");
				}
			});

			for (String s : list) {

				assertTrue(s.startsWith("a"));
			}
		}

		{
			List<String> list = $C.filter(l, new $C.Filter<String>() {
				@Override
				public boolean $(String v) {
					return v.matches("^[bc]");
				}
			});

			for (String s : list) {

				assertTrue(s.startsWith("b") || s.startsWith("c"));
			}
		}

	}

	@Test
	public void testFilterListChain() {

		List<String> l = new ArrayList<String>();
		l.add("aaa");
		l.add("aab");
		l.add("bbbb");
		l.add("ccc");
		l.add("   ");
		l.add("XXX");

		{
			$C.filter$(l, new $C.Filter<String>() {
				@Override
				public boolean $(String v) {
					return v.startsWith("a");
				}
			}).filter(new $C.Filter<String>() {
				@Override
				public boolean $(String v) {

					return v.endsWith("b");
				}
			}).foreach(new $C.Foreach<String>() {
				@Override
				public void $(String v) {
					assertEquals("MSG", v, "aab");
				}

			});

		}

		{
		}

	}

	@Test
	public void testFilterSet() {

		Set<String> l = new HashSet<String>();
		l.add("aaa");
		l.add("aab");
		l.add("bbbb");
		l.add("ccc");
		l.add("   ");
		l.add("XXX");

		{
			Set<String> list = $C.filter(l, new $C.Filter<String>() {
				@Override
				public boolean $(String v) {
					return v.startsWith("a");
				}
			});

			for (String s : list) {

				assertTrue(s.startsWith("a"));
			}
		}

		{
			Set<String> list = $C.filter(l, new $C.Filter<String>() {
				@Override
				public boolean $(String v) {
					return v.matches("^[bc]");
				}
			});

			for (String s : list) {

				assertTrue(s.startsWith("b") || s.startsWith("c"));
			}
		}

	}

	@Test
	public void testFilterTreeSet() {

		SortedSet<String> l = new TreeSet<String>();
		l.add("aaa");
		l.add("aab");
		l.add("bbbb");
		l.add("ccc");
		l.add("   ");
		l.add("XXX");

		{
			SortedSet<String> list = $C.filter(l, new $C.Filter<String>() {
				@Override
				public boolean $(String v) {
					return v.startsWith("a");
				}
			});

			for (String s : list) {

				assertTrue(s.startsWith("a"));
			}
		}

		{
			SortedSet<String> list = $C.filter(l, new $C.Filter<String>() {
				@Override
				public boolean $(String v) {
					return v.matches("^[bc]");
				}
			});

			for (String s : list) {

				assertTrue(s.startsWith("b") || s.startsWith("c"));
			}
		}

	}

	@Test
	public void testFilterTreeMap() {

		Map<String, String> l = new HashMap<String, String>();
		l.put("aaa", "aaa");
		l.put("aab", "aaa");
		l.put("bbbb", "aaa");
		l.put("ccc", "aaa");
		l.put("   ", "aaa");
		l.put("XXX", "aaa");

		{
			Map<String, String> list = $C.filter(l,
					new $C.FilterM<String, String>() {

						@Override
						public boolean $(Entry<String, String> v) {
							// TODO 自動生成されたメソッド・スタブ
							return v.getKey().startsWith("a");
						}

					});

			for (Entry<String, String> s : list.entrySet()) {

				assertTrue(s.getKey().startsWith("a"));
			}
		}

		{
			Map<String, String> list = $C.filter(l,
					new $C.FilterM<String, String>() {

						@Override
						public boolean $(Entry<String, String> v) {

							return v.getKey().matches("^[bc]");

						}

					});

			for (Entry<String, String> s : list.entrySet()) {

				assertTrue(s.getKey().startsWith("b")
						|| s.getKey().startsWith("c"));
			}
		}

	}

	@Test
	public void testForeach() {

	}

	@Test
	public void testFilter$() {

	}

	@Test
	public void testForeach$() {

	}

}

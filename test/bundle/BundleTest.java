package bundle;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Test;

public class BundleTest {
	@Test
	public void test1() {
		Locale myLocale = Locale.getDefault();// 获得系统默认的国家/语言环境
		ResourceBundle bundle = ResourceBundle.getBundle("languages.cms_admin.messages", myLocale);
		System.out.println(bundle);
		System.out.println(bundle.getString("login.username"));
	}
}

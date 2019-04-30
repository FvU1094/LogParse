package com.irm.parselog.parselog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class PattenTest {

	@Test
	public void test() {

		File file = new File("D:\\log\\2.log");
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String regex = "([\\s\\S]*) - ([\\s\\S]*) \\[([\\s\\S]*)\\] \"([\\s\\S]*)\" ([\\s\\S]*) ([\\s\\S]*) \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" ";
			Pattern pattern = Pattern.compile(regex);
			for (int i = 0; i < 10; i++) {
				String string = bufferedReader.readLine();

				Matcher matcher = pattern.matcher(string);
				if (matcher.find()) {
					for (int j = 1; j <= matcher.groupCount(); j++) {
						String t = matcher.group(j);
						System.out.println(t);
					}
				} else {
					System.out.print("error");
				}
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}

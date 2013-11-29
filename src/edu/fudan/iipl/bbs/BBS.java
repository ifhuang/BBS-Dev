package edu.fudan.iipl.bbs;

import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BBS {

	
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Document doc = Jsoup.connect("http://bbs.fudan.edu.cn/bbs/all").get();
		Elements all_brd = doc.getElementsByTag("brd");
		for (int i = 0; i < all_brd.size(); i++) {
			Element brd = all_brd.get(i);
			String title = brd.attr("title");
			String next = "http://bbs.fudan.edu.cn/bbs/doc?board=" + title;
			System.out.println(next);
		}

	}

}

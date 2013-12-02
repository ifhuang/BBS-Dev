package edu.fudan.iipl.bbs;

import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BBS
{
	private static int PO_NUM = 20;
	private static String BBS_ALL_URL = "http://bbs.fudan.edu.cn/bbs/all";
	private static String BRD = "brd";
	private static String DIR = "dir";
	private static String TITLE = "title";
	private static String BRD_LAST_PRE_URL = "http://bbs.fudan.edu.cn/bbs/doc?board=";
	private static String BID = "bid";
	private static String TOTAL = "total";
	private static String BRD_PRE_URL = "http://bbs.fudan.edu.cn/bbs/doc?bid=";
	private static String AND_START = "&start=";
	private static String PO = "po";
	private static String ID = "id";
	private static String PRE_URL = "http://bbs.fudan.edu.cn/bbs/con?new=1&bid=";
	private static String AND_F = "&f=";

	static void usage()
	{
		System.err.print("Usage: ");
		System.err.print(BBS.class.getName() + " ");
		System.err.print("[output_path]");
		System.exit(1);
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			usage();
		}
		FileWriter fw = new FileWriter(args[0]);
		Document bbs_all_doc = Jsoup.connect(BBS_ALL_URL).get();
		Elements all_brd = bbs_all_doc.getElementsByTag(BRD);
		long start_time = System.currentTimeMillis();
		for (int brd_index = 0; brd_index < all_brd.size(); brd_index++)
		{
			Element brd = all_brd.get(brd_index);
			String brd_dir = brd.attr(DIR);
			if (brd_dir.equals("1"))
			{
				continue;
			}
			String brd_title = brd.attr(TITLE);
			String brd_last_url = BRD_LAST_PRE_URL + brd_title;

			Document brd_last_doc = Jsoup.connect(brd_last_url).get();
			// secondary_brd.size() == 1
			Elements secondary_brds = brd_last_doc.getElementsByTag(BRD);
			if (secondary_brds.size() != 1)
			{
				System.err.println("secondary_brd.size()="
						+ secondary_brds.size());
				System.exit(1);
			}
			Element secondary_brd = secondary_brds.get(0);
			String bid = secondary_brd.attr(BID);
			int total = Integer.parseInt(secondary_brd.attr(TOTAL));
			int start = 1;

			System.out.print("Fetching brd_title=" + brd_title + ",bid=" + bid
					+ ",total=" + total + ",spend=");
			long start_brd_time = System.currentTimeMillis();
			while (start + PO_NUM <= total)
			{
				String brd_url = BRD_PRE_URL + bid + AND_START + start;
				Document brd_doc = Jsoup.connect(brd_url).get();
				Elements all_po = brd_doc.getElementsByTag(PO);
				// ignore sticky po
				for (int po_index = 0; po_index < PO_NUM; po_index++)
				{
					Element po = all_po.get(po_index);
					String id = po.attr(ID);
					String url = PRE_URL + bid + AND_F + id;
					fw.write(url);
					fw.write("\n");
				}
				start += PO_NUM;
			}

			// last
			String brd_url = BRD_PRE_URL + bid + AND_START + start;
			Document brd_doc = Jsoup.connect(brd_url).get();
			Elements all_po = brd_doc.getElementsByTag(PO);
			for (int po_index = PO_NUM - 1 - (total - start); po_index < PO_NUM; po_index++)
			{
				Element po = all_po.get(po_index);
				String id = po.attr(ID);
				String url = PRE_URL + bid + AND_F + id;
				fw.write(url);
				fw.write("\n");
			}
			long end_brd_time = System.currentTimeMillis();
			double brd_seconds = (end_brd_time - start_brd_time) / 1000.;
			System.out.println(brd_seconds + "s");

			fw.flush();
		}
		long end_time = System.currentTimeMillis();
		double seconds = (end_time - start_time) / 1000.;
		System.out.println("all spend:" + seconds + "s");
		fw.close();
	}

}

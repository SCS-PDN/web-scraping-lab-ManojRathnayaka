import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScraperServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) visitCount = 0;
        session.setAttribute("visitCount", visitCount + 1);
        String url = request.getParameter("url");
        boolean scrapeTitle = request.getParameter("title") != null;
        boolean scrapeLinks = request.getParameter("links") != null;
        boolean scrapeImages = request.getParameter("images") != null;
        List<Map<String, String>> scrapedDataList = scrapeData(url, scrapeTitle, scrapeLinks, scrapeImages);
        Gson gson = new Gson();
        String json = gson.toJson(scrapedDataList);
        response.setContentType("application/json");
        response.getWriter().write(json);
        response.getWriter().write("<br>You have visited this page " + visitCount + " times.");
        request.setAttribute("scrapedDataList", scrapedDataList);
        request.getRequestDispatcher("/downloadCSV.jsp").forward(request, response);
    }

    private List<Map<String, String>> scrapeData(String url, boolean scrapeTitle, boolean scrapeLinks, boolean scrapeImages) {
        List<Map<String, String>> dataList = new ArrayList<>();
        
        try {
        	Document doc = Jsoup.connect(url).get();
            if (scrapeTitle) {
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("Title", doc.title());
                dataList.add(dataMap);
            }
            if (scrapeLinks) {
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("Link", link.attr("href"));
                    dataList.add(dataMap);
                }
            }
            if (scrapeImages) {
                Elements images = doc.select("img[src]");
                for (Element image : images) {
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("Image", image.attr("src"));
                    dataList.add(dataMap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dataList;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("download".equals(action)) {
            List<Map<String, String>> scrapedDataList = (List<Map<String, String>>) request.getAttribute("scrapedDataList");
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=results.csv");
            PrintWriter writer = response.getWriter();
            for (Map<String, String> data : scrapedDataList) {
                for (String value : data.values()) {
                    writer.print(value + ",");
                }
                writer.println();
            }
        }
    }
}

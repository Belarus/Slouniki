package org.alex73.slouniki;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Simple extends Base {
    StringBuilder html = new StringBuilder();

    public static void main(String[] args) throws Exception {
        new Simple("be", "Беларуская энцыклапедыя, 1996-2004");
        new Simple("hsbm", "Гістарычны слоўнік беларускай мовы, 1982-2017");
        new Simple("esbm", "Этымалагічны слоўнік беларускай мовы, 1978-2017");
        new Simple("prykazki_tlum", "Тлумачальны слоўнік прыказак, 2011 / I. Я. Лепешаў, М. А. Якалцэвіч");
    }

    public Simple(String dict, String title) throws Exception {
        super("docs/" + dict, "templates-simple");
        copyFrom("files-simple/");

        if (dict.equals("prykazki_tlum")) {
            Files.list(Paths.get("templates-simple/")).filter(p -> p.getFileName().toString().matches("art\\-prykazki\\-[0-9]+\\.html")).sorted().forEach(p -> {
                art2("art-template.html", p, outputDir.resolve(p.getFileName()));
            });
        }

        read("data-simple/" + dict + ".txt", !dict.equals("prykazki_tlum"));

        replaces.put("TITLE", title);
        replaces.put("DICT", dict);
        replaces.put("INTROS", intros);
        replaces.put("ARTICLES", "<div id='articles'>" + html + "</div>");
        Files.writeString(outputDir.resolve("index.html"), output("index-template.html", replaces));
        errors.forEach(System.out::println);
    }

    static final Pattern PAGE = Pattern.compile("page(/2)?([\\+\\-][0-9]+)?");

    void read(String path, boolean hide) throws Exception {
        String url = "";
        int ps = -1;
        String pageMode = "";
        StringBuilder text = new StringBuilder();
        int page = -1;
        for (String line : Files.readAllLines(Paths.get(path))) {
            if (line.isBlank()) {
                continue;
            } else if (line.startsWith("URL:")) {
                url = line.substring(4).trim().replaceAll("_[^_]+$", "");
                pageMode = line.replaceAll(".+_", "");
            } else if (line.startsWith("Page:")) {
                flush(text, url, ps, page);
                page = Integer.parseInt(line.substring(5).trim());
            } else if (line.startsWith("@")) {
                flush(text, url, ps, page);
                String w = line.replaceAll("^@+", "").replace('´', '\u0301');
                text.append("<ah>" + w + "</ah> ");
                Matcher m = PAGE.matcher(pageMode);
                if (!m.matches()) {
                    throw new Exception(pageMode);
                }
                ps = page;
                if (m.group(1) != null) {
                    ps = ps / 2;
                }
                if (m.group(2) != null) {
                    ps = ps + Integer.parseInt(m.group(2));
                }
            } else {
                text.append(line.replace('´', '\u0301')).append("<br/>");
            }
        }
        flush(text, url, ps, page);
    }

    private void flush(StringBuilder s, String url, int ps, int p) {
        if (!s.isEmpty()) {
            html.append("<article" + (true ? " style='display:none'" : "") + ">" + s.toString().replaceAll("<br/>$", "") + " <a target='_blank' href='" + url
                    + "_" + ps + "'>с. " + p + "</a></article>\n");
            s.setLength(0);
        }
    }
}

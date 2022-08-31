package org.alex73.slouniki;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

/**
 * Дыякрытыка піньінь: \u0300\u0301\u0304\u0308\u030c Combining Diacritical
 * Marks 0300—036F Cyrillic 0400—04FF IPA Extensions 0250—02AF CJK Unified
 * Ideographs 4E00—9FFF ставіць ускочыць
 */
public class Kitajski extends Base {
    String kb, bk;

    public static void main(String[] args) throws Exception {
        new Kitajski();
    }

    static void d(String t) {
        t = Normalizer.normalize(t, Normalizer.Form.NFD);
        System.out.println(Arrays.toString(t.toCharArray()));
    }

    public Kitajski() throws Exception {
        super("docs/Kitajski", "templates-Kitajski");
        copyFrom("files-Kitajski/");

        Files.list(Paths.get("templates-Kitajski/")).filter(p -> p.getFileName().toString().matches("art[0-9]+\\.html")).sorted().forEach(p -> {
            art2("art-template.html", p, outputDir.resolve(p.getFileName()));
        });

        read();

        replaces.put("INTROS", intros);
        //replaces.put("ARTICLES", "<div id='kb'>" + kb + "</div><div id='bk'>" + bk + "</div>");
        replaces.put("ARTICLES", "<div id='articles'>" + kb + bk + "</div>");
        Files.writeString(outputDir.resolve("index.html"), output("index-template.html", replaces));
        errors.forEach(System.out::println);
    }

    void read() throws Exception {
        kb = Files.readString(Paths.get("data-Kitajski/Kitajska-bielaruski.html"));
        int p1 = kb.indexOf("<body>");
        int p2 = kb.indexOf("</body>");
        if (p1 < 0 || p2 < 0 || p1 >= p2) {
            throw new Exception();
        }
        kb = kb.substring(p1 + 6, p2).replace("<p>", "<article style='display: none'>").replace("</p>", "</article>");
        kb = Normalizer.normalize(kb, Normalizer.Form.NFD);
        bk = Files.readString(Paths.get("data-Kitajski/Bielaruska-kitajski.html"));
        p1 = bk.indexOf("<body>");
        p2 = bk.indexOf("</body>");
        if (p1 < 0 || p2 < 0 || p1 >= p2) {
            throw new Exception();
        }
        bk = bk.substring(p1 + 6, p2).replace("⏺", "\u25CF").replace("<p>", "<article style='display: none'>").replace("</p>", "</article>");
        bk = Normalizer.normalize(bk, Normalizer.Form.NFD);
    }
}

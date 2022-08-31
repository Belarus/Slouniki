package org.alex73.slouniki;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

abstract class Base {
    final Path outputDir;

    Configuration cfg;
    String intros = "";
    Map<String, String> replaces = new TreeMap<>();
    Set<String> errors = new TreeSet<>();

    Base(String outputDir, String templatesDir) throws IOException {
        this.outputDir = Paths.get(outputDir);
        if (Files.exists(this.outputDir)) {
            delete(this.outputDir);
        }
        Files.createDirectories(this.outputDir);

        cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateLoader(new MultiTemplateLoader(
                new TemplateLoader[] { new FileTemplateLoader(new File("templates")), new FileTemplateLoader(new File(templatesDir)) }));
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(true);
        cfg.setWrapUncheckedExceptions(true);
    }

    void delete(Path dir) throws IOException {
        for (Path p : Files.list(dir).toList()) {
            if (p.getFileName().toString().startsWith(".")) {
                continue;
            }
            if (Files.isDirectory(p)) {
                delete(p);
                if (Files.list(p).count() > 0) {
                    continue;
                }
            }
            Files.delete(p);
        }
    }

    String output(String templatePath, Object context) throws Exception {
        StringWriter out = new StringWriter();
        Template template = cfg.getTemplate(templatePath);
        template.process(context, out);
        return out.toString();
    }

    String readString(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    void copyFrom(String path) throws IOException {
        for (Path f : Files.list(Paths.get(path)).toList()) {
            Files.copy(f, outputDir.resolve(f.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    void art(String templatePath, Path text, Path out) {
        try {
            List<String> lines = Files.readAllLines(text);
            String title = lines.get(0);
            intros += "<p><a href='" + text.getFileName() + "'>" + lines.get(0) + "</a></p>\n";
            lines.set(0, "<h3>" + lines.get(0) + "</h3>");
            String html = "";
            for (String s : lines) {
                if (s.startsWith("<")) {
                    html += s + "\n";
                } else {
                    html += "<p>" + s + "</p>\n";
                }
            }
            // output(templatePath, Map.of("TITLE", title, "TEXT", html), out);
//            String o = readString(templatePath).replace("{{{TITLE}}}", title).replace("{{{TEXT}}}", html);
            // o = Normalizer.normalize(o.replaceAll("(.)´", "<u>$1</u>"),
            // Normalizer.Form.NFC);
            // Files.write(out, o.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    void art2(String templatePath, Path text, Path out) {
        String name = text.getFileName().toString().replaceAll("\\.[a-zA-Z0-9]+$", "");
        try {
            List<String> lines = Files.readAllLines(text);
            String title = lines.get(0);
            //lines.set(0, "<h3>" + lines.get(0) + "</h3>");
            lines.remove(0);
            String html = "";
            for (String s : lines) {
                if (s.startsWith("<")) {
                    html += s + "\n";
                } else {
                    html += "<p>" + s + "</p>\n";
                }
            }
            intros += "  <div class='accordion-item'>";
            intros += "    <div class='accordion-header' id='flush-heading" + name + "'>";
            intros += "      <a href='#' class='accordion-button2 collapsed' type='button' data-bs-toggle='collapse' data-bs-target='#flush-collapse" + name
                    + "' aria-expanded='false' aria-controls='flush-collapse" + name + "'>";
            intros += "         " + title;
            intros += "      </a>";
            intros += "    </div>";
            intros += "    <div id='flush-collapse" + name + "' class='accordion-collapse collapse' aria-labelledby='flush-heading" + name
                    + "' data-bs-parent='#accordionIntro'>";
            intros += "      <div class='accordion-body'>";
            intros += html;
            intros += "        <hr/>";
            intros += "      </div>";
            intros += "    </div>";
            intros += "  </div>";
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

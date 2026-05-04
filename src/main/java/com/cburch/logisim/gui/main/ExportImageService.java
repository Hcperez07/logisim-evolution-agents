/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.gui.main;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.gui.main.ExportImage.ImageFileFilter;
import com.cburch.logisim.gui.generic.TikZWriter;
import com.cburch.logisim.proj.Project;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;

public final class ExportImageService {
  private static final int BORDER_SIZE = 5;

  private ExportImageService() {}

  public static List<File> export(Project project, List<Circuit> circuits, ExportOptions options)
      throws IOException {
    final var exported = new ArrayList<File>();
    final var sorted = new ArrayList<>(circuits);
    sorted.sort(Comparator.comparing(Circuit::getName));
    for (final var circuit : sorted) {
      exported.add(exportSingle(project, circuit, options));
    }
    return exported;
  }

  private static File exportSingle(Project project, Circuit circuit, ExportOptions options)
      throws IOException {
    final var filter =
        options.format() == Format.SVG
            ? ExportImage.getFilter(ExportImage.FORMAT_SVG)
            : ExportImage.getFilter(ExportImage.FORMAT_PNG);
    final var where = targetFile(options.output(), circuit.getName(), filter);

    final var bounds = circuit.getBounds(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics()).expand(BORDER_SIZE);
    final var width = (int) Math.round(bounds.getWidth() * options.scale());
    final var height = (int) Math.round(bounds.getHeight() * options.scale());
    final var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    Graphics g;
    Graphics base;
    if (options.format() == Format.SVG) {
      base = new TikZWriter();
      g = base.create();
    } else {
      base = image.getGraphics();
      g = base.create();
      g.setColor(Color.white);
      g.fillRect(0, 0, width, height);
      g.setColor(Color.black);
    }

    if (g instanceof Graphics2D g2d) {
      g2d.scale(options.scale(), options.scale());
      g.translate(-bounds.getX(), -bounds.getY());
    } else {
      throw new IOException("Could not create image graphics context");
    }

    final var circuitState = project.getCircuitState(circuit);
    final var context =
        new ComponentDrawContext(project.getFrame().getCanvas(), circuit, circuitState, base, g, options.printerView());
    circuit.draw(context, null);

    if (options.format() == Format.SVG) {
      try {
        ((TikZWriter) g).writeSvg(width, height, where);
      } catch (Exception ex) {
        throw new IOException("SVG export failed", ex);
      }
    } else {
      ImageIO.write(image, "PNG", where);
    }
    g.dispose();
    return where;
  }

  private static File targetFile(File output, String circuitName, ImageFileFilter filter)
      throws IOException {
    if (output.isDirectory() || !filter.accept(output)) {
      if (!output.exists() && !output.mkdirs()) {
        throw new IOException("Failed to create output directory: " + output);
      }
      return new File(output, sanitizeFileName(circuitName) + filterExtension(filter));
    }
    return output;
  }

  private static String filterExtension(ImageFileFilter filter) {
    return filter.accept(new File("x.svg")) ? ".svg" : ".png";
  }

  private static String sanitizeFileName(String name) {
    return name.replaceAll("[^A-Za-z0-9._-]", "_");
  }
  public enum Format {
    PNG,
    SVG
  }

  public record ExportOptions(Format format, double scale, boolean printerView, File output) {}
}

